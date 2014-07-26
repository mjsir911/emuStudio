/*
 * PseudoORG.java
 *
 * Created on Sobota, 2007, september 29, 10:32
 *
 * Copyright (C) 2007-2012 Peter Jakubčo
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
package net.sf.emustudio.zilogZ80.assembler.tree;

import emulib.runtime.HEXFileManager;
import net.sf.emustudio.zilogZ80.assembler.impl.Namespace;
import net.sf.emustudio.zilogZ80.assembler.impl.NeedMorePassException;
import net.sf.emustudio.zilogZ80.assembler.treeAbstract.Expression;
import net.sf.emustudio.zilogZ80.assembler.treeAbstract.Pseudo;

public class PseudoORG extends Pseudo {

    private Expression expr;

    public PseudoORG(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public void pass1() {
    }

    // org only changes current address
    // if expr isnt valuable, then error exception is thrown
    // it cant help even more passes, because its recursive:
    // org label
    // mvi a,50
    // label: hlt
    // label address cant be evaluated
    @Override
    public int pass2(Namespace parentEnv, int addr_start) throws Exception {
        int val = addr_start;
        try {
            val = expr.eval(parentEnv, addr_start);
        } catch (NeedMorePassException e) {
            throw new Exception("[" + line + "," + column
                    + "] Error: ORG expression can't be ambiguous");
        }
        return val;
    }

    @Override
    public void pass4(HEXFileManager hex) throws Exception {
        hex.setNextAddress(expr.getValue());
    }
}