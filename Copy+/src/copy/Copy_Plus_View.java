/*
 * Copy_Plus_View.java
 */
package copy;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 *
 * @author Abhishek.D
 */
public class Copy_Plus_View extends JFrame implements Runnable {

    private List<String> Files = new ArrayList<String>();
    private List<String> Dirs = new ArrayList<String>();
    private List<Object> Saves = new ArrayList<Object>();
    private String[] files;
    private String Text;
    private String Str;
    String cur_path = System.getenv("HOME") + "\\Copy_Plus_Data";
    private File openFile = null;
    private boolean Saved = false;
    private Object text;
    private Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
    private int i,  j,  k,  l,  m,  n;
    private boolean exists = false;
    private boolean started = false;
    private Thread timer;
    private boolean running = false;
    private boolean NewL = false;
    private Integer Type;
    private File curFile;
    private File inFile;
    private File outFile;
    private File nFile = null;
    private InputStream ins = null;
    private OutputStream outs = null;
    private byte[] buf = new byte[1024];
    private int len;
    private String[] children;
    private Object[] fildir;
    private String[] preve;
    private String prev;
    private DefaultListModel L_M;
    private Help hlp;

    public void copyIt(File inF, File outF) {
        try {
            if (inF.isDirectory()) {
                if (!outF.exists()) {
                    outF.mkdir();
                    System.out.println("created!!!");
                }
                children = inF.list();
                for (n = 0; n < children.length; n++) {
                    copyIt(new File(inF + "\\" + children[n]), new File(outF + "\\" + children[n]));
                }
            } else {
                ins = new FileInputStream(inF);
                outs = new FileOutputStream(outF);

                while ((len = ins.read(buf)) > 0) {
                    outs.write(buf, 0, len);
                }

                ins.close();
                outs.close();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.exit(-1);
        }
    }

    public void paste() throws IOException {
        Prog_L.setValue(0);
        if (Type == 0) {
            if (File_Ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                for (k = 0; k < Files.size(); k++) {
                    curFile = new File(Files.get(k));
                    inFile = new File(Files.get(k));
                    outFile = new File(File_Ch.getSelectedFile() + "\\" + curFile.getName());
                    if (outFile.getPath().contentEquals(inFile.getPath())) {
                        JOptionPane.showMessageDialog(this, "The Files cannot be pasted to Source!!!", "Error", JOptionPane.PLAIN_MESSAGE);
                        break;
                    }
                    copyIt(inFile, outFile);
                }
                if (!outFile.getPath().contentEquals(inFile.getPath())) {
                    JOptionPane.showMessageDialog(this, "The Files are pasted to Destination!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } else if (Type == 1) {
            k = 0;
            if (File_Ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                for (k = 0; k < Dirs.size(); k++) {
                    curFile = new File(Dirs.get(k));
                    inFile = new File(Dirs.get(k));
                    outFile = new File(File_Ch.getSelectedFile() + "\\" + curFile.getName());
                    if (outFile.getPath().contentEquals(inFile.getPath())) {
                        JOptionPane.showMessageDialog(this, "The Files cannot be pasted to Source!!!", "Error", JOptionPane.PLAIN_MESSAGE);
                        break;
                    }
                    copyIt(inFile, outFile);
                }
                if (!outFile.getPath().contentEquals(inFile.getPath())) {
                    JOptionPane.showMessageDialog(this, "The Directories are pasted to Destination!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
                }
            }
        } else if (Type == 2) {
            StringSelection ss = new StringSelection(Str);
            Prog_L.setValue(50);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Error : " + e);
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
            Prog_L.setValue(100);
            JOptionPane.showMessageDialog(this, "The Text is pasted to Clipboard!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
        }
    }

    public void run() {
        while (true) {
            if (Type == 2) {
                Lab.setText("Text : ");
                cur_File.setText("Check Edit");
                NewL_Ch.setEnabled(true);
            } else {
                NewL_Ch.setEnabled(false);
                Lab.setText("File : ");
            }
            if (Prog_L.getValue() != 0) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }
                Prog_L.setValue(0);
            }
            if (running) {
                Type = Type_C.getSelectedIndex();
                try {
                    try {
                        t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
                    } catch (Exception e) {
                        System.out.println("Interrupted!!!");
                    }
                    if (Type == 0) {
                        try {
                            exists = false;
                            if (t != null && t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                text = t.getTransferData(DataFlavor.javaFileListFlavor);

                                Text = text.toString().replace("[", "");
                                Text = Text.toString().replace("]", "");

                                files = Text.toString().split(",");
                                if (!Arrays.equals(preve, files)) {
                                    preve = files;
                                    for (i = 0; i < files.length; i++) {
                                        if (Files.indexOf(files[i].trim()) != -1) {
                                            exists = true;
                                        }
                                        if (!exists) {
                                            if (!(files[i].trim().contentEquals(prev))) {
                                                Files.add(files[i].trim());
                                                prev = files[i].trim();
                                                System.out.println(Files.get(Files.size() - 1) + "\n");
                                                cur_File.setText(Files.get(Files.size() - 1));
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error : " + e);
                        }
                    } else if (Type == 1) {
                        try {
                            exists = false;
                            if (t != null && t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                text = t.getTransferData(DataFlavor.javaFileListFlavor);

                                Text = text.toString().replace("[", "");
                                Text = Text.toString().replace("]", "");

                                files = Text.toString().split(",");
                                if (!Arrays.equals(preve, files)) {
                                    preve = files;
                                    for (i = 0; i < files.length; i++) {
                                        for (j = 0; j < Dirs.size(); j++) {
                                            if (Dirs.get(j).contentEquals(files[i].trim())) {
                                                exists = true;
                                            }
                                        }
                                        if (!exists) {
                                            if (!(files[i].trim().contentEquals(prev))) {
                                                Dirs.add(files[i].trim());
                                                prev = files[i].trim();
                                                System.out.println(Dirs.get(Dirs.size() - 1) + "\n");
                                                cur_File.setText(Dirs.get(Dirs.size() - 1));
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Error : " + e);
                        }
                    } else if (Type == 2) {
                        if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                            if (Str.indexOf((String) t.getTransferData(DataFlavor.stringFlavor)) == -1) {
                                if (NewL) {
                                    Str += "\n";
                                }
                                Str += (String) t.getTransferData(DataFlavor.stringFlavor);
                            }
                            System.out.println("Text : " + Str + "\n");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error : " + e);
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }
            }
        }
    }

    public void start() {
        timer = new Thread(this);
        timer.start();
    }

    public void stop() {
        timer = null;
    }

    public void fullClear() {
        StringSelection ss = new StringSelection("");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        Prog_L.setValue(50);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("Error : " + e);
        }

        Files.clear();
        Dirs.clear();
        Str = "";

        preve = ("null null null").split(" ");

        Prog_L.setValue(100);
    }

    void saveData(List<Object> st, File sofi) {
        try {
            FileOutputStream of = new FileOutputStream(sofi);
            ObjectOutputStream os = new ObjectOutputStream(of);
            os.writeObject(st);
            os.flush();
            os.close();
            of.close();
        } catch (Exception e) {
            System.err.println("\n" + e.getMessage());
        }
    }

    void saveData(String st, File sofi) {
        try {
            FileOutputStream of = new FileOutputStream(sofi);
            ObjectOutputStream os = new ObjectOutputStream(of);
            os.writeObject(st);
            os.flush();
            os.close();
            of.close();
        } catch (Exception e) {
            System.err.println("\n" + e.getMessage());
        }
    }

    List<Object> readDataL(File sofi) {
        ArrayList<Object> data = null;
        if (sofi.exists()) {
            try {
                FileInputStream ifs = new FileInputStream(sofi);
                ObjectInputStream is = new ObjectInputStream(ifs);
                data = (ArrayList<Object>) is.readObject();
                is.close();
                ifs.close();
            } catch (Exception e) {
                System.err.println("\n" + e.getMessage());
            }
        } else {
            System.err.println("\nFile doesnt exit!!!");
            System.exit(0);
        }
        return data;
    }

    /** Creates new form Copy_Plus_View */
    public Copy_Plus_View(String[] filePass) {
        initComponents();
        fullClear();
        hlp = new Help();
        L_M = new DefaultListModel();
        prev = "";
        boolean ip;
        preve = ("null null null").split(" ");
        System.out.println(new File(System.getenv("HOME") + "\\Copy_Plus_View").exists() + ", " + new File(System.getenv("HOME") + "\\Copy_Plus_View").getPath());
        if (!(new File(System.getenv("HOME") + "\\Copy_Plus_View").exists())) {
            System.out.println(System.getenv("HOME") + "\\Copy_Plus_Data");
            try {
                if (ip = new File(System.getenv("HOME") + "\\Copy_Plus_Data").mkdir()) {
                    System.out.println("Data Directory Created!!!");
                } else {
                    System.out.println(ip);
                }

            } catch (Exception e) {
                System.out.println("Error : " + e);
            }

        }

        try {
            this.setIconImage(ImageIO.read(getClass().getResource("/copy/copy.png")));
            hlp.setIconImage(ImageIO.read(getClass().getResource("/copy/copy.png")));
            Edit_W.setIconImage(ImageIO.read(getClass().getResource("/copy/copy.png")));
            Edit_W2.setIconImage(ImageIO.read(getClass().getResource("/copy/copy.png")));
            About_W.setIconImage(ImageIO.read(getClass().getResource("/copy/copy.png")));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        hlp.setVisible(false);
        hlp.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        hlp.setTitle("Help");
        Edit_W.setSize(400, 200);
        Edit_W.setVisible(false);
        Edit_W2.setSize(400, 200);
        Edit_W2.setVisible(false);
        About_W.setSize(661, 295);
        About_W.setVisible(false);
        Type =
                Type_C.getSelectedIndex();
        start();

        Text =
                "";
        Str =
                "";
        if (filePass[0].length() > 0) {
            Saves = readDataL(new File(filePass[0]));
            Files = (List<String>) Saves.get(0);
            Dirs = (List<String>) Saves.get(1);
            Str = (String) Saves.get(2);
            Saved = true;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        File_Ch = new javax.swing.JFileChooser();
        Edit_W = new javax.swing.JFrame();
        Clear_B2 = new javax.swing.JButton();
        Close_B2 = new javax.swing.JButton();
        View_P = new javax.swing.JScrollPane();
        View_L = new javax.swing.JList();
        Edit_W2 = new javax.swing.JFrame();
        Clear_B3 = new javax.swing.JButton();
        Close_B3 = new javax.swing.JButton();
        Edit_B2 = new javax.swing.JButton();
        View_P1 = new javax.swing.JScrollPane();
        Edit_T = new javax.swing.JEditorPane();
        About_W = new javax.swing.JFrame();
        javax.swing.JLabel imageLabel = new javax.swing.JLabel();
        javax.swing.JLabel versionLabel = new javax.swing.JLabel();
        javax.swing.JLabel vendorLabel = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel = new javax.swing.JLabel();
        javax.swing.JLabel appVersionLabel = new javax.swing.JLabel();
        javax.swing.JLabel appVendorLabel = new javax.swing.JLabel();
        javax.swing.JLabel appHomepageLabel = new javax.swing.JLabel();
        javax.swing.JLabel appTitleLabel = new javax.swing.JLabel();
        javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel appHomepageLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        Start_B = new javax.swing.JButton();
        Paste_B = new javax.swing.JButton();
        Clear_B = new javax.swing.JButton();
        Close_B = new javax.swing.JButton();
        Type_C = new javax.swing.JComboBox();
        Prog_L = new javax.swing.JProgressBar();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        Edit_B = new javax.swing.JButton();
        cur_File = new javax.swing.JTextField();
        Lab = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        NewL_Ch = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        Auto = new javax.swing.JCheckBoxMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        File_Ch.setApproveButtonToolTipText("Save");
        File_Ch.setCurrentDirectory(new java.io.File("C:\\Users\\Dinesan\\Documents\\Copy_Plus_Data"));
        File_Ch.setDialogTitle("Copy Files To.....");
        File_Ch.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        File_Ch.setFileSelectionMode(javax.swing.JFileChooser.FILES_AND_DIRECTORIES);
        File_Ch.setForeground(java.awt.Color.white);

        Edit_W.setTitle("Editor");

        Clear_B2.setText("Clear");
        Clear_B2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Clear_B2ActionPerformed(evt);
            }
        });

        Close_B2.setText("Close");
        Close_B2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Close_B2ActionPerformed(evt);
            }
        });

        View_L.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        View_P.setViewportView(View_L);

        javax.swing.GroupLayout Edit_WLayout = new javax.swing.GroupLayout(Edit_W.getContentPane());
        Edit_W.getContentPane().setLayout(Edit_WLayout);
        Edit_WLayout.setHorizontalGroup(
            Edit_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Edit_WLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(Clear_B2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 186, Short.MAX_VALUE)
                .addComponent(Close_B2)
                .addGap(55, 55, 55))
            .addComponent(View_P, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        Edit_WLayout.setVerticalGroup(
            Edit_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Edit_WLayout.createSequentialGroup()
                .addComponent(View_P, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Edit_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Clear_B2)
                    .addComponent(Close_B2))
                .addContainerGap())
        );

        Edit_W2.setTitle("Editor");

        Clear_B3.setText("Clear");
        Clear_B3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Clear_B3ActionPerformed(evt);
            }
        });

        Close_B3.setText("Close");
        Close_B3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Close_B3ActionPerformed(evt);
            }
        });

        Edit_B2.setText("Edit");
        Edit_B2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Edit_B2ActionPerformed(evt);
            }
        });

        View_P1.setViewportView(Edit_T);

        javax.swing.GroupLayout Edit_W2Layout = new javax.swing.GroupLayout(Edit_W2.getContentPane());
        Edit_W2.getContentPane().setLayout(Edit_W2Layout);
        Edit_W2Layout.setHorizontalGroup(
            Edit_W2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Edit_W2Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(Clear_B3)
                .addGap(61, 61, 61)
                .addComponent(Edit_B2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addComponent(Close_B3)
                .addGap(55, 55, 55))
            .addComponent(View_P1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        Edit_W2Layout.setVerticalGroup(
            Edit_W2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Edit_W2Layout.createSequentialGroup()
                .addComponent(View_P1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Edit_W2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Clear_B3)
                    .addComponent(Close_B3)
                    .addComponent(Edit_B2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        About_W.setTitle("About");

        imageLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copy/abhi.jpg"))); // NOI18N

        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() & ~java.awt.Font.BOLD, versionLabel.getFont().getSize()+3));
        versionLabel.setText("Product Version\\:");

        vendorLabel.setFont(vendorLabel.getFont().deriveFont(vendorLabel.getFont().getStyle() & ~java.awt.Font.BOLD, vendorLabel.getFont().getSize()+3));
        vendorLabel.setText("Creator\\:");

        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() & ~java.awt.Font.BOLD, homepageLabel.getFont().getSize()+3));
        homepageLabel.setText("Age\\:");

        appVersionLabel.setFont(appVersionLabel.getFont().deriveFont(appVersionLabel.getFont().getStyle() | java.awt.Font.BOLD, appVersionLabel.getFont().getSize()+3));
        appVersionLabel.setText("1.0");

        appVendorLabel.setFont(appVendorLabel.getFont().deriveFont(appVendorLabel.getFont().getStyle() | java.awt.Font.BOLD, appVendorLabel.getFont().getSize()+3));
        appVendorLabel.setText("Abhishek.D");

        appHomepageLabel.setFont(appHomepageLabel.getFont().deriveFont(appHomepageLabel.getFont().getStyle() | java.awt.Font.BOLD, appHomepageLabel.getFont().getSize()+3));
        appHomepageLabel.setText("16");

        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize()+3));
        appTitleLabel.setText("Copy+");

        appDescLabel.setFont(appDescLabel.getFont().deriveFont(appDescLabel.getFont().getStyle() & ~java.awt.Font.BOLD, appDescLabel.getFont().getSize()+3));
        appDescLabel.setText("<html>An Application designed to ease massive selective copying, in order to reduce the pain of redoing the whole thing if something goes wrong.");

        homepageLabel1.setFont(homepageLabel1.getFont().deriveFont(homepageLabel1.getFont().getStyle() & ~java.awt.Font.BOLD, homepageLabel1.getFont().getSize()+3));
        homepageLabel1.setText("Year\\:");

        appHomepageLabel1.setFont(appHomepageLabel1.getFont().deriveFont(appHomepageLabel1.getFont().getStyle() | java.awt.Font.BOLD, appHomepageLabel1.getFont().getSize()+3));
        appHomepageLabel1.setText("2014");

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout About_WLayout = new javax.swing.GroupLayout(About_W.getContentPane());
        About_W.getContentPane().setLayout(About_WLayout);
        About_WLayout.setHorizontalGroup(
            About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(About_WLayout.createSequentialGroup()
                .addComponent(imageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(appTitleLabel, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(appDescLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, About_WLayout.createSequentialGroup()
                        .addGroup(About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(versionLabel)
                            .addComponent(vendorLabel)
                            .addComponent(homepageLabel)
                            .addComponent(homepageLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appHomepageLabel1)
                            .addComponent(appVersionLabel)
                            .addComponent(appVendorLabel)
                            .addComponent(appHomepageLabel)))
                    .addComponent(jButton1))
                .addContainerGap())
        );
        About_WLayout.setVerticalGroup(
            About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(About_WLayout.createSequentialGroup()
                .addComponent(appTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appDescLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionLabel)
                    .addComponent(appVersionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vendorLabel)
                    .addComponent(appVendorLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(homepageLabel)
                    .addComponent(appHomepageLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(About_WLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(homepageLabel1)
                    .addComponent(appHomepageLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Copy+");

        Start_B.setMnemonic('1');
        Start_B.setText("Start");
        Start_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Start_BActionPerformed(evt);
            }
        });

        Paste_B.setText("Paste");
        Paste_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Paste_BActionPerformed(evt);
            }
        });

        Clear_B.setText("Clear");
        Clear_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Clear_BActionPerformed(evt);
            }
        });

        Close_B.setText("Close");
        Close_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Close_BActionPerformed(evt);
            }
        });

        Type_C.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Files", "Directories", "Text" }));
        Type_C.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Type_CActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12));
        jLabel1.setText("Progress : ");

        Edit_B.setText("Edit");
        Edit_B.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Edit_BActionPerformed(evt);
            }
        });

        cur_File.setEditable(false);
        cur_File.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cur_FileActionPerformed(evt);
            }
        });

        Lab.setFont(new java.awt.Font("Tahoma", 1, 12));
        Lab.setText("File : ");

        NewL_Ch.setText("New_Line");
        NewL_Ch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NewL_ChActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem11.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem11.setText("New");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem9.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem9.setText("Open");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem9);
        jMenu1.add(jSeparator3);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem8.setText("Save");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem10.setText("Save As");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);
        jMenu1.add(jSeparator4);

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setText("Exit");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Actions");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        jMenuItem4.setText("Start");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, 0));
        jMenuItem5.setText("Paste");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem6.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        jMenuItem6.setText("Edit");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0));
        jMenuItem7.setText("Clear");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem7);
        jMenu3.add(jSeparator5);

        Auto.setSelected(true);
        Auto.setText("Auto_Clear_On_Paste");
        Auto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AutoActionPerformed(evt);
            }
        });
        jMenu3.add(Auto);

        jMenuBar1.add(jMenu3);

        jMenu2.setText("Help");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setText("Help");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuItem3.setText("About");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(Start_B)
                                .addGap(26, 26, 26)
                                .addComponent(Paste_B)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addComponent(Edit_B)
                                .addGap(29, 29, 29)
                                .addComponent(Clear_B)
                                .addGap(31, 31, 31)
                                .addComponent(Close_B)
                                .addGap(11, 11, 11))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(Type_C, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(NewL_Ch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(Prog_L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(Lab)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cur_File, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Start_B)
                    .addComponent(Close_B)
                    .addComponent(Clear_B)
                    .addComponent(Edit_B)
                    .addComponent(Paste_B))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(Prog_L, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                            .addComponent(Type_C, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NewL_Ch))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cur_File, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Lab))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Close_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Close_BActionPerformed
        System.out.println("Main_Close");
        if (JOptionPane.showConfirmDialog(this, "You are about to Exit!!!\nYou will lose all current data!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
            running = false;
            stop();

            System.exit(0);
        }
    }//GEN-LAST:event_Close_BActionPerformed

    private void Start_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Start_BActionPerformed
        System.out.println("Main_Start");
        if (!started) {
            start();
            Start_B.setText("Stop");
            started =
                    true;
            running =
                    true;
        } else {
            Start_B.setText("Start");
            started = false;
            running = false;
        }
    }//GEN-LAST:event_Start_BActionPerformed

    private void Type_CActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Type_CActionPerformed
        System.out.println("Main_Type_Changed");
        Type = Type_C.getSelectedIndex();
        StringSelection ss = new StringSelection("");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }//GEN-LAST:event_Type_CActionPerformed

    private void Paste_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Paste_BActionPerformed
        System.out.println("Main_Paste");
        Start_B.setText("Start");
        started = false;
        running = false;
        if (!started) {
            try {
                paste();
                StringSelection ss = new StringSelection("");
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                Prog_L.setValue(50);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }

                if (Type == 0) {
                    Files.clear();
                    cur_File.setText("");
                } else if (Type == 1) {
                    Dirs.clear();
                    cur_File.setText("");
                } else if (Type == 2) {
                    Str = "";
                }

                preve = ("null null null").split(" ");

                Prog_L.setValue(100);
            } catch (IOException e) {
                System.out.println("Error : " + e);
            }
        }
    }//GEN-LAST:event_Paste_BActionPerformed

    private void Clear_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Clear_BActionPerformed
        System.out.println("Main_Clear");
        if (!started) {
            if (JOptionPane.showConfirmDialog(this, "This will Clear Clipboard and Database!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
                StringSelection ss = new StringSelection("");
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                Prog_L.setValue(50);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }

                if (Type == 0) {
                    Files.clear();
                    cur_File.setText("");
                } else if (Type == 1) {
                    Dirs.clear();
                    cur_File.setText("");
                } else if (Type == 2) {
                    Str = "";
                }

                preve = ("null null null").split(" ");

                Prog_L.setValue(100);
                JOptionPane.showMessageDialog(this, "Clipboard and Database Cleared!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Copy must be stopped before this!!!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_Clear_BActionPerformed

    private void Edit_BActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Edit_BActionPerformed
        System.out.println("Main_Edit");
        if (!started) {
            Prog_L.setValue(0);
            if (Type == 0) {
                try {
                    L_M.clear();
                    View_L.setModel(L_M);

                    System.out.println(Files.size() + "\n");
                    for (m = 0; m <
                            Files.size(); m++) {
                        try {
                            L_M.addElement(Files.get(m));
                        } catch (Exception e) {
                            System.out.println("uu : " + e);
                        }

                        System.out.println("Files : " + Files.get(m));
                    }

                    View_P.setViewportView(View_L);
                    Edit_B2.setVisible(false);
                } catch (Exception e) {
                    System.out.println("Error : " + e);
                }

            } else if (Type == 1) {
                try {
                    L_M.clear();
                    View_L.setModel(L_M);

                    System.out.println(Dirs.size() + "\n");
                    for (m = 0; m <
                            Dirs.size(); m++) {
                        try {
                            L_M.addElement(Dirs.get(m));
                        } catch (Exception e) {
                            System.out.println("uu : " + e);
                        }

                        System.out.println("Files : " + Dirs.get(m));
                    }

                    View_P.setViewportView(View_L);
                    Edit_B2.setVisible(false);
                } catch (Exception e) {
                    System.out.println("Error : " + e);
                }

            } else if (Type == 2) {
                Edit_T.setText(Str);
                Edit_B2.setVisible(true);
            }

            if (Type == 2) {
                Edit_W2.setVisible(true);
            } else {
                Edit_W.setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Copy must be stopped before this!!!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_Edit_BActionPerformed

    private void Close_B2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Close_B2ActionPerformed
        System.out.println("Edit_Close");
        Edit_W.setVisible(false);
    }//GEN-LAST:event_Close_B2ActionPerformed

    private void Clear_B2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Clear_B2ActionPerformed
        System.out.println("Edit_Clear");
        if (Type != 2) {
            if (View_L.getSelectedValue() == null) {
                if (JOptionPane.showConfirmDialog(this, "This will Clear Clipboard and Database of all data!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
                    StringSelection ss = new StringSelection("");
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                    Prog_L.setValue(50);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println("Error : " + e);
                    }

                    if (Type == 0) {
                        Files.clear();
                    } else if (Type == 1) {
                        Dirs.clear();
                    }

                    Edit_W.setVisible(false);

                    Prog_L.setValue(100);
                    JOptionPane.showMessageDialog(this, "Clipboard and Database Cleared!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
                }

            } else {
                if (JOptionPane.showConfirmDialog(this, "This will Clear Clipboard and Database of selected data!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
                    StringSelection ss = new StringSelection("");
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                    Prog_L.setValue(50);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println("Error : " + e);
                    }

                    fildir = View_L.getSelectedValues();

                    for (l = 0; l < fildir.length; l++) {
                        if (Type == 0) {
                            Files.remove(fildir[l].toString());
                        } else if (Type == 1) {
                            Dirs.remove(fildir[l].toString());
                        }

                    }
                    Edit_W.setVisible(false);

                    Prog_L.setValue(100);
                    JOptionPane.showMessageDialog(this, "Clipboard and Database Cleared!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
                }

            }
        } else {
            if (JOptionPane.showConfirmDialog(this, "This will Clear Clipboard and Database of all data!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
                StringSelection ss = new StringSelection("");
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                Prog_L.setValue(50);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }

                Str = "";
                Prog_L.setValue(100);
                JOptionPane.showMessageDialog(this, "Clipboard and Database Cleared!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
            }

        }
    }//GEN-LAST:event_Clear_B2ActionPerformed

    private void Edit_B2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Edit_B2ActionPerformed
        System.out.println("Edit_2_Edit");
        if (JOptionPane.showConfirmDialog(this, "This will Edit Database!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
            Prog_L.setValue(50);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Error : " + e);
            }

            Str = Edit_T.getText();
            Prog_L.setValue(100);
            JOptionPane.showMessageDialog(this, "Database successfully edited!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
        }
    }//GEN-LAST:event_Edit_B2ActionPerformed

    private void Close_B3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Close_B3ActionPerformed
        System.out.println("Edit_2_Close");
        Edit_W2.setVisible(false);
    }//GEN-LAST:event_Close_B3ActionPerformed

    private void Clear_B3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Clear_B3ActionPerformed
        System.out.println("Edit_2_Clear");
        if (Type == 2) {
            if (JOptionPane.showConfirmDialog(this, "This will Clear Clipboard and Database of all data!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
                StringSelection ss = new StringSelection("");
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                Prog_L.setValue(50);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }

                Str = "";
                cur_File.setText("");
                Prog_L.setValue(100);
                JOptionPane.showMessageDialog(this, "Clipboard and Database Cleared!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
            }

        }
}//GEN-LAST:event_Clear_B3ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        About_W.setVisible(true);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        About_W.setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        hlp.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void cur_FileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cur_FileActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_cur_FileActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        System.out.println("Main_Start");
        if (!started) {
            start();
            Start_B.setText("Stop");
            started =
                    true;
            running =
                    true;
        } else {
            Start_B.setText("Start");
            started =
                    false;
            running =
                    false;
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        System.out.println("Main_Paste");
        Start_B.setText("Start");
        started =
                false;
        running =
                false;
        if (!started) {
            try {
                paste();
            } catch (IOException e) {
                System.out.println("Error : " + e);
            }
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        System.out.println("Main_Edit");
        if (!started) {
            Prog_L.setValue(0);
            if (Type == 0) {
                try {
                    L_M.clear();
                    View_L.setModel(L_M);

                    System.out.println(Files.size() + "\n");
                    for (m = 0; m <
                            Files.size(); m++) {
                        try {
                            L_M.addElement(Files.get(m));
                        } catch (Exception e) {
                            System.out.println("uu : " + e);
                        }

                        System.out.println("Files : " + Files.get(m));
                    }

                    View_P.setViewportView(View_L);
                    Edit_B2.setVisible(false);
                } catch (Exception e) {
                    System.out.println("Error : " + e);
                }

            } else if (Type == 1) {
                try {
                    L_M.clear();
                    View_L.setModel(L_M);

                    System.out.println(Dirs.size() + "\n");
                    for (m = 0; m <
                            Dirs.size(); m++) {
                        try {
                            L_M.addElement(Dirs.get(m));
                        } catch (Exception e) {
                            System.out.println("uu : " + e);
                        }

                        System.out.println("Files : " + Dirs.get(m));
                    }

                    View_P.setViewportView(View_L);
                    Edit_B2.setVisible(false);
                } catch (Exception e) {
                    System.out.println("Error : " + e);
                }

            } else if (Type == 2) {
                Edit_T.setText(Str);
                Edit_B2.setVisible(true);
            }

            if (Type == 2) {
                Edit_W2.setVisible(true);
            } else {
                Edit_W.setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Copy must be stopped before this!!!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        System.out.println("Main_Clear");
        if (!started) {
            if (JOptionPane.showConfirmDialog(this, "This will Clear Clipboard and Database!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
                StringSelection ss = new StringSelection("");
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                Prog_L.setValue(50);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }

                if (Type == 0) {
                    Files.clear();
                    cur_File.setText("");
                } else if (Type == 1) {
                    Dirs.clear();
                    cur_File.setText("");
                } else if (Type == 2) {
                    Str = "";
                }

                preve = ("null null null").split(" ");

                Prog_L.setValue(100);
                JOptionPane.showMessageDialog(this, "Clipboard and Database Cleared!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Copy must be stopped before this!!!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        System.out.println("Main_Close");
        if (JOptionPane.showConfirmDialog(this, "You are about to Exit!!!\nYou will lose all current data!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
            running = false;
            stop();

            System.exit(0);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        if (Saved) {
            if (Saves.size() > 0) {
                Saves.clear();
            }

            Saves.add(0, Files);
            Saves.add(1, Dirs);
            Saves.add(2, Str);
            saveData(Saves, openFile);
            Saved = true;
        } else {
            if (nFile == null) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Copy_Data(.cpsd)", "cpsd");
                File_Ch.setFileFilter(filter);
                File_Ch.setCurrentDirectory(new File(cur_path));
                if (File_Ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    if (Saves.size() > 0) {
                        Saves.clear();
                    }

                    Saves.add(0, Files);
                    Saves.add(1, Dirs);
                    Saves.add(2, Str);

                    if (!(File_Ch.getSelectedFile().toString().endsWith(".cpsd"))) {
                        saveData(Saves, new File(File_Ch.getSelectedFile() + ".cpsd"));
                        openFile = new File(File_Ch.getSelectedFile() + ".cpsd");
                        Saved = true;
                        cur_path = File_Ch.getSelectedFile().getAbsolutePath();
                    } else {
                        saveData(Saves, new File(File_Ch.getSelectedFile() + ""));
                        openFile = new File(File_Ch.getSelectedFile() + "");
                        Saved = true;
                        cur_path = File_Ch.getSelectedFile().getAbsolutePath();
                    }
                }
            }
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Copy_Data(.cpsd)", "cpsd");
        File_Ch.setFileFilter(filter);
        File_Ch.setCurrentDirectory(new File(cur_path));
        if (File_Ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            Saves = readDataL(File_Ch.getSelectedFile());
            Files = (List<String>) Saves.get(0);
            Dirs = (List<String>) Saves.get(1);
            Str = (String) Saves.get(2);
            Saved = true;
            cur_path = File_Ch.getSelectedFile().getAbsolutePath();
        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        if (nFile == null) {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Copy_Data(.cpsd)", "cpsd");
            File_Ch.setFileFilter(filter);
            File_Ch.setCurrentDirectory(new File(cur_path));
            if (File_Ch.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (Saves.size() > 0) {
                    Saves.clear();
                }

                Saves.add(0, Files);
                Saves.add(1, Dirs);
                Saves.add(2, Str);

                if (!(File_Ch.getSelectedFile().toString().endsWith(".cpsd"))) {
                    saveData(Saves, new File(File_Ch.getSelectedFile() + ".cpsd"));
                    openFile = new File(File_Ch.getSelectedFile() + ".cpsd");
                    Saved = true;
                    cur_path = File_Ch.getSelectedFile().getAbsolutePath();
                } else {
                    saveData(Saves, new File(File_Ch.getSelectedFile() + ""));
                    openFile = new File(File_Ch.getSelectedFile() + "");
                    Saved = true;
                    cur_path = File_Ch.getSelectedFile().getAbsolutePath();
                }
            }
        }
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        System.out.println("Main_Clear");
        if (!started) {
            if (JOptionPane.showConfirmDialog(this, "This will Clear Clipboard and Database!!!\nAre you sure?", "Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
                StringSelection ss = new StringSelection("");
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
                Prog_L.setValue(50);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("Error : " + e);
                }

                if (Type == 0) {
                    Files.clear();
                    cur_File.setText("");
                } else if (Type == 1) {
                    Dirs.clear();
                    cur_File.setText("");
                } else if (Type == 2) {
                    Str = "";
                }

                preve = ("null null null").split(" ");

                Prog_L.setValue(100);
                JOptionPane.showMessageDialog(this, "Clipboard and Database Cleared!!!", "Action Performed", JOptionPane.PLAIN_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Copy must be stopped before this!!!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }
        Saved = false;
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void NewL_ChActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NewL_ChActionPerformed
        if (Type == 2) {
            if (NewL) {
                NewL = false;
                System.out.println("New_Line_Disabled");
            } else {
                NewL = true;
                System.out.println("New_Line_Enabled");
            }
        }
}//GEN-LAST:event_NewL_ChActionPerformed

    private void AutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AutoActionPerformed
        if (Auto.getState()) {
            System.out.println("Auto_Clear_Disabled");
        } else {
            System.out.println("Auto_Clear_Enabled");
        }
    }//GEN-LAST:event_AutoActionPerformed
    /**
     * @param args the command line arguments
     */
    static String[] passFile = {""};

    public static void main(String args[]) {
        if (args.length > 0) {
            passFile = args;
            System.out.println(passFile[0]);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Copy_Plus_View view = new Copy_Plus_View(passFile);
                view.setVisible(true);
                view.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFrame About_W;
    private javax.swing.JCheckBoxMenuItem Auto;
    private javax.swing.JButton Clear_B;
    private javax.swing.JButton Clear_B2;
    private javax.swing.JButton Clear_B3;
    private javax.swing.JButton Close_B;
    private javax.swing.JButton Close_B2;
    private javax.swing.JButton Close_B3;
    private javax.swing.JButton Edit_B;
    private javax.swing.JButton Edit_B2;
    private javax.swing.JEditorPane Edit_T;
    private javax.swing.JFrame Edit_W;
    private javax.swing.JFrame Edit_W2;
    private javax.swing.JFileChooser File_Ch;
    private javax.swing.JLabel Lab;
    private javax.swing.JCheckBox NewL_Ch;
    private javax.swing.JButton Paste_B;
    private javax.swing.JProgressBar Prog_L;
    private javax.swing.JButton Start_B;
    private javax.swing.JComboBox Type_C;
    private javax.swing.JList View_L;
    private javax.swing.JScrollPane View_P;
    private javax.swing.JScrollPane View_P1;
    private javax.swing.JTextField cur_File;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    // End of variables declaration//GEN-END:variables
}