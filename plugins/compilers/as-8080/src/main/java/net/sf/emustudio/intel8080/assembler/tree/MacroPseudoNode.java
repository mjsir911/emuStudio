/*
 * Copyright (C) 2007-2015 Peter Jakubčo
 *
 * KISS, YAGNI, DRY
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package net.sf.emustudio.intel8080.assembler.tree;

import emulib.runtime.HEXFileManager;
import net.sf.emustudio.intel8080.assembler.exceptions.InvalidMacroParamsCountException;
import net.sf.emustudio.intel8080.assembler.exceptions.UnknownMacroParametersException;
import net.sf.emustudio.intel8080.assembler.impl.CompileEnv;
import net.sf.emustudio.intel8080.assembler.treeAbstract.ExprNode;
import net.sf.emustudio.intel8080.assembler.treeAbstract.PseudoBlock;

import java.util.ArrayList;
import java.util.List;

public class MacroPseudoNode extends PseudoBlock {
    private CompileEnv newEnv;
    private final List<String> params; // macro parameters
    private List<ExprNode> call_params; // concrete parameters, they can change
    private final Statement stat;
    private final String mnemo;

    public MacroPseudoNode(String name, List<String> params, Statement s, int line, int column) {
        super(line, column);
        this.mnemo = name;
        if (params == null) {
            this.params = new ArrayList<>();
        } else {
            this.params = params;
        }
        this.stat = s;
    }

    @Override
    public String getName() {
        return mnemo;
    }

    public void setCallParams(List<ExprNode> params) {
        this.call_params = params;
    }

    @Override
    public int getSize() {
        return 0;
    }

    public int getStatSize() {
        return stat.getSize();
    }

    @Override
    public void pass1() throws Exception {
        stat.pass1(); // pass1 creates block symbol table (local for block)
    }
    
    // this is macro expansion ! can be called only in MacroCallPseudo class
    // call parameters have to be set
    @Override
    public int pass2(CompileEnv env, int addr_start) throws Exception {
        newEnv = new CompileEnv(env.getInputFile().getAbsolutePath());
        // add local statement env to newEnv
        stat.getCompileEnv().copyTo(newEnv);
        env.copyTo(newEnv); // add parent statement env to newEnv
        // remove all existing definitions of params name (from level-up environment)
        for (String param : params) {
            newEnv.removeAllDefinitions(param);
        }
        // check of call_params
        if (call_params == null) {
            throw new UnknownMacroParametersException(line, column, mnemo);
        }
        if (call_params.size() != params.size()) {
            throw new InvalidMacroParamsCountException(line, column, mnemo, params.size(), call_params.size());
        }
        // create/rewrite symbols => parameters as equ pseudo instructions
        for (int i = 0; i < params.size(); i++) {
            newEnv.addConstant(new EquPseudoNode(params.get(i), call_params.get(i),
                    line, column));
        }
        return stat.pass2(newEnv, addr_start);
    }

    @Override
    public void pass4(HEXFileManager hex) throws Exception {
        stat.pass4(hex, newEnv);
    }
}
