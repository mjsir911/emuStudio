/*
 * frmMemory.java
 *
 * Created on Nede�a, 2007, okt�ber 28, 10:40
 */

package gui;

import gui.utils.EmuFileFilter;
import gui.utils.memoryTableModel;
import gui.utils.tableMemory;
import interfaces.SMemoryContext;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import runtime.StaticDialogs;


/**
 *
 * @author  vbmacher
 */
public class frmMemory extends javax.swing.JFrame {
    private SMemoryContext mem;
    private tableMemory tblMemory;
    private memoryTableModel memModel;
    
    /** Creates new form frmMemory */
    public frmMemory(SMemoryContext mem) {
        this.mem = mem;
        this.memModel = new memoryTableModel(mem);
        
        initComponents();
        tblMemory = new tableMemory(memModel,paneMemory);
        paneMemory.setViewportView(tblMemory);
        tblMemory.setVisible(true);
        paneMemory.repaint();
        memModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                ((SpinnerNumberModel)spnPage.getModel()).setValue(memModel.getPage());
            }
        });
        lblPageCount.setText(String.valueOf(memModel.getPageCount()));
        spnPage.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int i = (Integer)((SpinnerNumberModel)spnPage.getModel()).getValue();
                try { memModel.setPage(i); } catch(IndexOutOfBoundsException ex) {
                    ((SpinnerNumberModel)spnPage.getModel())
                    .setValue(memModel.getPage());
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                destroyME();
            }
        });

        memModel.addTableModelListener(new TableModelListener() {
             public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                updateMemVal(row, column);
            }
        });
        
        tblMemory.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblMemory.rowAtPoint(e.getPoint());
                int col = tblMemory.columnAtPoint(e.getPoint());
                updateMemVal(row, col);
            }
        });
        this.setLocationRelativeTo(null);
    }
    
    public void updateMemVal(int row, int column) {
        if (tblMemory.isCellSelected(row, column) == false) return;
        int address = memModel.getRowCount() * memModel.getColumnCount()
            * memModel.getPage()+ row * memModel.getColumnCount() + column;

        int data = Integer.parseInt(memModel.getValueAt(row, column).toString(),16);
        txtAddress.setText(String.format("%04X", address));
        txtChar.setText(String.format("%c", data & 0xFF));
        txtValDec.setText(String.format("%02d", data));
        txtValHex.setText(String.format("%02X", data));
        txtValOct.setText(String.format("%02o", data));
        txtValBin.setText(Integer.toBinaryString(data));
    }
    
    private void destroyME() { dispose(); }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnClearMemory = new javax.swing.JButton();
        btnOpenImage = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        spnPage = new javax.swing.JSpinner();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        lblPageCount = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnFindAddress = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnROMRanges = new javax.swing.JButton();
        paneMemory = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtChar = new javax.swing.JTextField();
        txtValDec = new javax.swing.JTextField();
        txtValHex = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtValOct = new javax.swing.JTextField();
        txtValBin = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Operating memory plugin");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnClearMemory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Delete24.gif"))); // NOI18N
        btnClearMemory.setToolTipText("Clear memory");
        btnClearMemory.setFocusable(false);
        btnClearMemory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearMemoryActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClearMemory);

        btnOpenImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Open24.gif"))); // NOI18N
        btnOpenImage.setToolTipText("Load image");
        btnOpenImage.setFocusable(false);
        btnOpenImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenImageActionPerformed(evt);
            }
        });
        jToolBar1.add(btnOpenImage);
        jToolBar1.add(jSeparator2);

        jLabel1.setText("Page number:");
        jToolBar1.add(jLabel1);

        spnPage.setMinimumSize(new java.awt.Dimension(70, 28));
        spnPage.setPreferredSize(new java.awt.Dimension(70, 28));
        jToolBar1.add(spnPage);
        jToolBar1.add(jSeparator4);

        jLabel2.setText("Page count:");
        jToolBar1.add(jLabel2);

        lblPageCount.setFont(lblPageCount.getFont().deriveFont(lblPageCount.getFont().getStyle() | java.awt.Font.BOLD));
        lblPageCount.setText("0");
        jToolBar1.add(lblPageCount);
        jToolBar1.add(jSeparator3);

        btnFindAddress.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Find24.gif"))); // NOI18N
        btnFindAddress.setToolTipText("Find address");
        btnFindAddress.setFocusable(false);
        btnFindAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindAddressActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFindAddress);
        jToolBar1.add(jSeparator1);

        btnROMRanges.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/History24.gif"))); // NOI18N
        btnROMRanges.setToolTipText("ROM ranges");
        btnROMRanges.setFocusable(false);
        btnROMRanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnROMRangesActionPerformed(evt);
            }
        });
        jToolBar1.add(btnROMRanges);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Memory value"));

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel3.setText("Address:");

        txtAddress.setEditable(false);
        txtAddress.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtAddress.setText("0000");

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel4.setText("Value:");

        jLabel5.setFont(jLabel5.getFont().deriveFont(jLabel5.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel5.setText("Char:");

        txtChar.setEditable(false);

        txtValDec.setEditable(false);
        txtValDec.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValDec.setText("00");

        txtValHex.setEditable(false);
        txtValHex.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValHex.setText("00");

        jLabel6.setText("(dec)");

        jLabel7.setText("(hex)");

        txtValOct.setEditable(false);
        txtValOct.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValOct.setText("000");

        txtValBin.setEditable(false);
        txtValBin.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtValBin.setText("00000000");

        jLabel8.setText("(oct)");

        jLabel9.setText("(bin)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtChar)
                    .addComponent(txtAddress, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtValHex)
                    .addComponent(txtValDec, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtValBin)
                    .addComponent(txtValOct, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(txtValDec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtValOct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtValHex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtValBin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(txtChar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(paneMemory, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paneMemory, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenImageActionPerformed
        JFileChooser f = new JFileChooser();
        EmuFileFilter f1 = new EmuFileFilter();
        EmuFileFilter f2 = new EmuFileFilter();

        f1.addExtension("hex");
        f1.addExtension("bin");
        f1.setDescription("Image file (*.hex, *.bin)");
        f2.addExtension("*");
        f2.setDescription("All files (*.*)");
        
        f.setDialogTitle("Load an image");
        f.setAcceptAllFileFilterUsed(false);
        f.addChoosableFileFilter(f1);
        f.addChoosableFileFilter(f2);
        f.setFileFilter(f1);
        f.setApproveButtonText("Load");

        int returnVal = f.showOpenDialog(this);
        f.setVisible(true);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File fileSource = f.getSelectedFile();
            if (fileSource.canRead() == true) {
                if (fileSource.getName().toLowerCase().endsWith(".hex"))
                    mem.loadHex(fileSource.getAbsolutePath());
                else {
                    // ask for address where to load image
                    int adr = 0;
                    String sadr = JOptionPane.showInputDialog("Enter starting address:", 0);
                    try { adr = Integer.decode(sadr); }
                    catch(NumberFormatException e) {}
                    mem.loadBin(fileSource.getAbsolutePath(),adr);
                }
                this.tblMemory.revalidate();
                this.tblMemory.repaint();
            } else {
                StaticDialogs.showErrorMessage("File " + fileSource.getPath()
                    + " can't be read.");
            }
        }
}//GEN-LAST:event_btnOpenImageActionPerformed

    private void btnClearMemoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearMemoryActionPerformed
       mem.clear();
       tblMemory.revalidate();
       tblMemory.repaint();
}//GEN-LAST:event_btnClearMemoryActionPerformed

    private void btnROMRangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnROMRangesActionPerformed
        new frmROMRange(mem, this.tblMemory).setVisible(true);
}//GEN-LAST:event_btnROMRangesActionPerformed
    
    private void btnFindAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindAddressActionPerformed
        int address = 0;
        try {
            address = Integer.decode(JOptionPane.showInputDialog(this,
                    "Find address:","Find Address",
                    JOptionPane.QUESTION_MESSAGE,null,null,0).toString()).intValue();
        } catch(NumberFormatException e) {return;} catch (NullPointerException f) {return;}
        if (address <0 || address >= mem.getSize()) {
            JOptionPane.showMessageDialog(this,"Error: Address out of bounds",
                    "Find Address",JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.memModel.setPage(address /
                (memModel.getRowCount() * memModel.getColumnCount()));
}//GEN-LAST:event_btnFindAddressActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearMemory;
    private javax.swing.JButton btnFindAddress;
    private javax.swing.JButton btnOpenImage;
    private javax.swing.JButton btnROMRanges;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblPageCount;
    private javax.swing.JScrollPane paneMemory;
    private javax.swing.JSpinner spnPage;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtChar;
    private javax.swing.JTextField txtValBin;
    private javax.swing.JTextField txtValDec;
    private javax.swing.JTextField txtValHex;
    private javax.swing.JTextField txtValOct;
    // End of variables declaration//GEN-END:variables
    
}
