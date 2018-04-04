/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examples.com.intelligt.modbus.examples;

import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.serial.SerialPortException;
import java.sql.Timestamp;
import javax.swing.JOptionPane;
import jssc.SerialPortList;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import static examples.com.intelligt.modbus.examples.MainForm.addedDeviceCount;
import static examples.com.intelligt.modbus.examples.MainForm.totaldev;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Shaikhs
 */
public class ActivatedScreen extends javax.swing.JFrame {

    public static SerialParameters sp = new SerialParameters();
    public static ModbusMaster m;//= ModbusMasterFactory.createModbusMasterRTU(sp);
    public static int tag = 0;

    public static void openPortRAS(String port) throws ModbusIOException {
        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        String[] dev_list = SerialPortList.getPortNames();
        // if there is at least one serial port at your system
        if ("".equals(port)) {
            sp.setDevice(dev_list[0]);
        } else {
            sp.setDevice(port);
        }
        sp.setBaudRate(SerialPort.BaudRate.BAUD_RATE_9600);
        sp.setDataBits(8);
        sp.setParity(SerialPort.Parity.NONE);
        sp.setStopBits(1);
        try {
            m = ModbusMasterFactory.createModbusMasterRTU(sp);
        } catch (SerialPortException ex) {
            Logger.getLogger(SimpleMasterRTU.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            m.connect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            m.disconnect();
        }
    }

    public void readModbusRAS(int sid, int channelcount) throws ModbusIOException, ModbusProtocolException, ModbusNumberException, InterruptedException {
        //int sid=1,valuetowrite=0;
        int slaveId = sid;
        int offset = 63;
        int quantity = 2;

        int[] relay1;
        int[] relay2;
        int[] relay3;

        m.writeSingleRegister(slaveId, 15, 0);
        Thread.sleep(10);
        relay1 = m.readHoldingRegisters(slaveId, 31, 1);

        m.writeSingleRegister(slaveId, 15, 1);
        Thread.sleep(10);
        relay2 = m.readHoldingRegisters(slaveId, 31, 1);

        m.writeSingleRegister(slaveId, 15, 2);
        Thread.sleep(10);
        relay3 = m.readHoldingRegisters(slaveId, 31, 1);

        R1[sid] = relay1[0];
        R2[sid] = relay2[0];
        R3[sid] = relay3[0];

        System.out.println("Relay1=" + relay1[0] + "Relay2=" + relay2[0] + "Relay3=" + relay3[0]);

        if (channelcount == 1) {
            m.writeSingleRegister(slaveId, 52, 0);
            Thread.sleep(500);

            int[] reg6364 = m.readHoldingRegisters(slaveId, offset, quantity);
            c1p1val[sid] = Math.round(inttofloat(reg6364) * 1000000);
            System.out.println("Para channel 1 = " + c1p1val[sid]);

            int[] reg6768 = m.readHoldingRegisters(slaveId, 67, 2);
            t1[sid] = inttofloat(reg6768);

            System.out.println("temp channel 1 = " + t1[sid]);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Connection con = JdbcSQLiteConnection.createConnection();
            Statement st = null;
            try {
                st = con.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(SimpleMasterRTU.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                st.execute("insert into TotalLog (timestmp,dev_no,c1r63,c1r67,c1re1,c1re2,c1re3)"
                        + " values('" + timestamp + "'," + sid + "," + c1p1val[sid] + "," + t1[sid] + ""
                        + "" + relay1[0] + "," + relay2[0] + "," + relay3[0] + ")");

                System.out.println("Insert Success");
            } catch (SQLException ex) {

                Logger.getLogger(SimpleMasterRTU.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (channelcount == 2) {
            m.writeSingleRegister(slaveId, 52, 0);
            Thread.sleep(500);

            int[] reg6364 = m.readHoldingRegisters(slaveId, offset, quantity);
            c1p1val[sid] = Math.round(inttofloat(reg6364) * 1000000);
            System.out.println("Para channel 1 = " + c1p1val[sid]);

            int[] reg6768 = m.readHoldingRegisters(slaveId, 67, 2);
            t1[sid] = inttofloat(reg6768);

            System.out.println("temp channel 1 = " + t1[sid]);

            //---------------------------------------------------------------------------------
            Thread.sleep(10);
            m.writeSingleRegister(slaveId, 52, 1);
            Thread.sleep(500);

            int[] reg6364_2 = m.readHoldingRegisters(slaveId, offset, quantity);
            c2p2val[sid] = inttofloat(reg6364_2);
            System.out.println("Para channel 2 = " + c2p2val[sid]);

            int[] reg6768_2 = m.readHoldingRegisters(slaveId, 67, 2);
            t2[sid] = inttofloat(reg6768_2);

            System.out.println("temp channel 2 = " + t2[sid]);

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Connection con = JdbcSQLiteConnection.createConnection();
            Statement st = null;
            try {
                st = con.createStatement();
            } catch (SQLException ex) {
                Logger.getLogger(SimpleMasterRTU.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                st.execute("insert into TotalLog (timestmp,dev_no,c1r63,c1r67,c1re1,c1re2,c1re3,c2r63,c2r67)"
                        + " values('" + timestamp + "'," + sid + "," + c1p1val[sid] + "," + t1[sid] + ","
                        + "" + relay1[0] + "," + relay2[0] + "," + relay3[0] + ""
                        + "" + c2p2val[sid] + "," + t2[sid] + ")");
                System.out.println("Insert Success");
            } catch (SQLException ex) {
                Logger.getLogger(SimpleMasterRTU.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    //import java.util.Scanner;
    public static float inttofloat(int num[]) {

        int HB[] = new int[16];
        int LB[] = new int[16];
        int index = 0, binindex = 15;

        //System.out.println("num 0 =  "+num[0]);
        //System.out.println("num 1 =  "+num[1]);
        while (num[0] > 0) {
            HB[binindex] = num[0] % 2;
            num[0] = num[0] / 2;
            binindex--;
        }

        index = 0;
        binindex = 15;
        while (num[1] > 0) {
            LB[binindex] = num[1] % 2;
            num[1] = num[1] / 2;
            binindex--;
        }

        int sign = 1, j = 0, k = 0;
        int fullword[] = new int[23];

        for (j = 9, k = 0; k < 7; j++, k++) {
            fullword[k] = HB[j];

        }

        for (j = 7, k = 0; k < 16; k++, j++) {
            fullword[j] = LB[k];

        }

//           System.out.println("Matissa="+Arrays.toString(fullword));
        if (HB[0] == 0) {
            sign = 1;

        } else {
            sign = -1;
        }

        int edash[] = new int[8], i;
        for (i = 1; i < 9; i++) {
            edash[i - 1] = HB[i];
        }

        int decimal = 0;
        int n = 0;
        for (i = 7; i >= 0; i--) {
            decimal += edash[i] * Math.pow(2, n);
            n++;
        }
        int newexpo = decimal - 127;

        decimal = 0;
        n = 0;
        for (i = 22; i >= 0; i--) {
            decimal += fullword[i] * Math.pow(2, n);
            n++;
        }
        float mant = 0;
        mant = (float) decimal / 8388608 + 1;
        float expodash = (float) Math.pow(2, newexpo);
        double newfloat = 0;
        newfloat = (mant * expodash * (float) sign);
        return (float) newfloat;
    }

    /**
     * Creates new form ActivatedScreen
     */
    public ActivatedScreen() {
        //this.model = (DefaultTableModel) jTableReading.getModel();
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableReading = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableRelay = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        cname = new javax.swing.JLabel();
        EUName = new javax.swing.JLabel();
        PlanName = new javax.swing.JLabel();
        ProName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDeviceLogs = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButtonReports = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButtonStop = new javax.swing.JButton();
        jButtonGraph = new javax.swing.JButton();
        jButtonSettings = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemAddDevice = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenuItem3.setText("jMenuItem3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Steam Industries Pvt. Ltd. - Modbus Software");
        setBackground(new java.awt.Color(204, 255, 204));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("\\icon\\TitleBarLogo.png")));
        setName("frameAct"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                portclose(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 102, 0));
        jLabel13.setText("Realtime Data Analysis");

        jTableReading.setBackground(new java.awt.Color(204, 255, 204));
        jTableReading.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jTableReading.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Device Name", "Device ID", "Device Type", "Parameter", "Value", "Temperature"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, true, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableReading.setToolTipText("");
        jTableReading.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableReading.setGridColor(new java.awt.Color(0, 0, 51));
        jTableReading.setOpaque(false);
        jTableReading.setRowHeight(25);
        jTableReading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableReadingMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableReading);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jScrollPane2))
                .addGap(0, 0, 0))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/examples/com/intelligt/modbus/examples/Icon/SteamLogo&Name_300x127.jpg"))); // NOI18N
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jPanel5.setBackground(new java.awt.Color(204, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setMaximumSize(new java.awt.Dimension(102, 385));
        jPanel5.setMinimumSize(new java.awt.Dimension(102, 385));

        jLabel14.setBackground(new java.awt.Color(204, 255, 255));
        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 51, 51));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Relay");

        jTableRelay.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTableRelay.setFont(new java.awt.Font("Segoe UI Symbol", 1, 14)); // NOI18N
        jTableRelay.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "R1", "R2", "R3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableRelay.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTableRelay.setFocusable(false);
        jTableRelay.setGridColor(new java.awt.Color(102, 0, 204));
        jTableRelay.setOpaque(false);
        jTableRelay.setRowHeight(25);
        jTableRelay.setRowMargin(8);
        jTableRelay.setSelectionBackground(new java.awt.Color(255, 255, 255));
        jTableRelay.setUpdateSelectionOnSort(false);
        jTableRelay.setVerifyInputWhenFocusTarget(false);
        jScrollPane3.setViewportView(jTableRelay);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(0, 74, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel14)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.activeTitleGradient"));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        cname.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cname.setForeground(new java.awt.Color(0, 102, 102));

        EUName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        EUName.setForeground(new java.awt.Color(0, 102, 102));

        PlanName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        PlanName.setForeground(new java.awt.Color(0, 102, 102));

        ProName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        ProName.setForeground(new java.awt.Color(0, 102, 102));

        jLabel2.setBackground(new java.awt.Color(0, 51, 51));
        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Customer Name:");

        jLabel4.setBackground(new java.awt.Color(0, 51, 51));
        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("End User Name:");

        jLabel5.setBackground(new java.awt.Color(0, 51, 51));
        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Plant Name:");

        jLabel7.setBackground(new java.awt.Color(0, 51, 51));
        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setText("Process:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PlanName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(EUName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ProName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cname, javax.swing.GroupLayout.PREFERRED_SIZE, 369, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cname, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(EUName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(PlanName, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ProName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        jTextAreaDeviceLogs.setColumns(20);
        jTextAreaDeviceLogs.setRows(5);
        jTextAreaDeviceLogs.setText("2018-04-01 15:02:04.044 - Device 1 Connected.\n2018-04-01 15:11:20.197 - Device 2 Connected.");
        jScrollPane1.setViewportView(jTextAreaDeviceLogs);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Device Logs");

        jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.green, new java.awt.Color(204, 255, 204), java.awt.Color.green, java.awt.Color.green));

        jButtonReports.setBackground(new java.awt.Color(255, 255, 255));
        jButtonReports.setForeground(new java.awt.Color(255, 255, 255));
        jButtonReports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/examples/com/intelligt/modbus/examples/Icon/70x70_report.png"))); // NOI18N
        jButtonReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReportsActionPerformed(evt);
            }
        });

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/examples/com/intelligt/modbus/examples/Icon/70x70start.jpg"))); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/examples/com/intelligt/modbus/examples/Icon/70x70stop.jpg"))); // NOI18N
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });

        jButtonGraph.setIcon(new javax.swing.ImageIcon(getClass().getResource("/examples/com/intelligt/modbus/examples/Icon/70x70graph.jpg"))); // NOI18N
        jButtonGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGraphActionPerformed(evt);
            }
        });

        jButtonSettings.setIcon(new javax.swing.ImageIcon("F:\\Project Repository\\jlibmodbus\\src\\examples\\com\\intelligt\\modbus\\examples\\Icon\\70x70setting.png")); // NOI18N
        jButtonSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSettingsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonReports, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonStop, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonStop, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonGraph, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonReports, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jMenuBar1.setBackground(new java.awt.Color(204, 255, 204));
        jMenuBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jMenuBar1.setForeground(new java.awt.Color(204, 255, 204));
        jMenuBar1.setOpaque(false);
        jMenuBar1.setSelectionModel(null);

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/examples/com/intelligt/modbus/examples/Icon/MenuLogo.png"))); // NOI18N
        jMenu1.setToolTipText("Open Menu");

        jMenuItemAddDevice.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        jMenuItemAddDevice.setText("Add Device");
        jMenuItemAddDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAddDeviceActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemAddDevice);

        jMenu3.setText("Settings");

        jMenuItem4.setText("Set Port");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenu1.add(jMenu3);

        jMenuItem5.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem5.setText("Activate");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText("Register");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenu2.setText("Help");

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem7.setText("About");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenu1.add(jMenu2);

        jMenuItem8.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        jMenuItem8.setText("Exit");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(0, 0, 0)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("frameAct");

        pack();
    }// </editor-fold>//GEN-END:initComponents
 public static String dev_name[] = new String[247];
    public static int devID[] = new int[247];
    public static String devType[] = new String[247];
    public static String c1p1[] = new String[247];
    public static String c1u1[] = new String[247];
    public static String c2p2[] = new String[247];
    public static String c2u2[] = new String[247];
    public static float c1p1val[] = new float[247], c2p2val[] = new float[247], t1[] = new float[247], t2[] = new float[247];
    public static int R1[] = new int[247], R2[] = new int[247], R3[] = new int[247];
    public static int chanelCount[] = new int[20], totalrowsintable = 0;
    public static int noofdev = 0, stopflag = 0;

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated

    }//GEN-LAST:event_formWindowActivated

    public void populateTableFix() {

        DefaultTableModel model = (DefaultTableModel) jTableReading.getModel();
        jTableReading.setRowHeight(30);
        // model.setRowCount(0);
        // jTableReading = new JTable(model);

        DefaultTableModel Relaymodel = (DefaultTableModel) jTableRelay.getModel();
        jTableRelay.setRowHeight(30);

        for (int i = 1; i <= noofdev; i++) {
            if ("LXT-330 Dual Channel".equals(devType[i])) {
                model.setValueAt(c1p1val[i] + " " + c1u1[i], i - 1, 4);
                model.setValueAt(t1[i] + " ˚C", i - 1, 5);
                model.setValueAt(c2p2val[i] + " " + c2u2[i], i, 4);
                model.setValueAt(t2[i] + " ˚C", i, 5);

            } else {
                model.setValueAt(c1p1val[i] + " " + c1u1[i], i - 1, 4);
                model.setValueAt(t1[i] + " ˚C", i - 1, 5);
            }

            Relaymodel.setValueAt(R1[i], i - 1, 0);
            Relaymodel.setValueAt(R2[i], i - 1, 1);
            Relaymodel.setValueAt(R3[i], i - 1, 2);

            jTableRelay.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer());
            jTableRelay.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
            jTableRelay.getColumnModel().getColumn(2).setCellRenderer(new CustomRenderer());
        }

    }

    class CustomRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 6703872492730589499L;

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (table.getValueAt(row, column).toString().equals("1")) {
                cellComponent.setBackground(Color.GREEN);
            }
            //if (table.getValueAt(row, column).toString().equals("1")) 
            else
            {
                cellComponent.setBackground(Color.RED);
            }

            return cellComponent;
        }
    }

    public class ReadThread extends Thread {

        @Override
        public void run() {
            Connection con = JdbcSQLiteConnection.createConnection();

            String query = "SELECT * FROM portTotalDev";

            Statement st;
            String portnm;

            try {
                st = con.createStatement();

                ResultSet rs = st.executeQuery(query);

                portnm = rs.getString("portname");
                noofdev = rs.getInt("noofdev");
                //    System.out.print("In thread = "+portnm+"total dev = "+noofdev);
                con.close();

                try {
                    openPortRAS(portnm);
                } catch (ModbusIOException ex) {
                    Logger.getLogger(ActivatedScreen.class.getName()).log(Level.SEVERE, null, ex);
                }

                // for (int j = 1; j < 2; j++)//infinite
                for (;;) {
                    if (stopflag == 1) {

                        _portclose();
                        stopflag = 0;
                        break;

                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ActivatedScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   // System.out.print("Channel ara= " + Arrays.toString(chanelCount));
                    for (int i = noofdev; i >= 1; i--) { //no of devices

                        try {

                            if (chanelCount[i] == 2) {
                                readModbusRAS(i, 2);

                            } else {
                                //   Thread.sleep(10);
                                readModbusRAS(i, 1);

                            }
                            populateTableFix();
                            jTableRelay.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer());
                            jTableRelay.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
                            jTableRelay.getColumnModel().getColumn(2).setCellRenderer(new CustomRenderer());
                        } catch (ModbusIOException | ModbusProtocolException | ModbusNumberException | InterruptedException ex) {
                            Logger.getLogger(ActivatedScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }

                //_portclose();
            } catch (SQLException ex) {
                System.out.println(ex);
            }
        }

    }

    private void _portclose() {
        try {
            m.disconnect();        // TODO add your handling code here:
            System.out.println("Port close");
        } catch (ModbusIOException ex) {
            Logger.getLogger(ActivatedScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void portclose(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_portclose
        _portclose();
    }//GEN-LAST:event_portclose

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        try {
            // TODO add your handling code here:

            Connection con = JdbcSQLiteConnection.createConnection();
            Statement st;
            ResultSet rs;

            String query = "SELECT * FROM portTotalDev";

            st = con.createStatement();
            rs = st.executeQuery(query);
            MainForm.port1 = rs.getString("portname");
            //  System.out.print("POrt read fro db="+MainForm.port1);

            query = "SELECT * FROM DeviceDetails";

            st = con.createStatement();
            rs = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) jTableReading.getModel();
            jTableReading.setRowHeight(30);
            model.setRowCount(0);
            //  jTableReading = new JTable(model);

            DefaultTableModel Relaymodel = (DefaultTableModel) jTableRelay.getModel();
            jTableRelay.setRowHeight(30);
            // model.setRowCount(0);
            Relaymodel.setRowCount(0);
            int i = 1;
            try {
                while (rs.next()) {
                    dev_name[i] = rs.getString("dev_name");
                    devID[i] = rs.getInt("dev_ID");
                    devType[i] = rs.getString("DevType");
                    c1p1[i] = rs.getString("c1para");
                    c1u1[i] = rs.getString("c1unit");
                    c2p2[i] = rs.getString("c2para");
                    c2u2[i] = rs.getString("c2unit");

                    chanelCount[i] = rs.getInt("no_channel");
                    System.out.print("Channel Count arrray" + chanelCount[i] + ",");
                    // Thread.sleep(500);
                    model.addRow(new Object[]{dev_name[i], devID[i], devType[i], c1p1[i], " ", " ˚C"});
                    Relaymodel.addRow(new Object[]{"0", "0", "0"});

                    if ("LXT-330 Dual Channel".equals(devType[i])) {
                        model.addRow(new Object[]{dev_name[i], devID[i], devType[i], c2p2[i], " ", " ˚C"});
                        Relaymodel.addRow(new Object[]{"-", "-", "-"});
                    }
                    i++;

                    //System.out.format("\n%s   %s   %s   %s   %s    %s",dev_name,devType,c1p1,c1u1,c2p2,c2u2);
                }
                jTableRelay.getColumnModel().getColumn(0).setCellRenderer(new CustomRenderer());
                jTableRelay.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer());
                jTableRelay.getColumnModel().getColumn(2).setCellRenderer(new CustomRenderer());
                totalrowsintable = i;
                //Read customer

                query = "SELECT * FROM CustomerDetails";

                st = con.createStatement();
                rs = st.executeQuery(query);
                cname.setText(rs.getString("Cname"));
                EUName.setText(rs.getString("EUName"));
                PlanName.setText(rs.getString("PlantName"));
                ProName.setText(rs.getString("ProName"));

                con.close();
            } catch (Exception e) {
                System.out.print(e);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ActivatedScreen.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_formWindowOpened

    private void jButtonSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSettingsActionPerformed

        new setPort().setVisible(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonSettingsActionPerformed

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        // TODO add your handling code here:
        stopflag = 1;
    }//GEN-LAST:event_jButtonStopActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        stopflag = 0;
        new ReadThread().start();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButtonGraphActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGraphActionPerformed
        rowselected = jTableReading.getSelectedRow();

        String paraname = (String) jTableReading.getValueAt(jTableReading.getSelectedRow(), 3);
        String dev_name = (String) jTableReading.getValueAt(jTableReading.getSelectedRow(), 2);
        int did = (int) jTableReading.getValueAt(jTableReading.getSelectedRow(), 1);
        JOptionPane.showMessageDialog(null, "Selected a row  " + rowselected + ":" + paraname + ":" + dev_name + ":" + did, "Error", JOptionPane.INFORMATION_MESSAGE);

        new ThreadTwice(paraname, dev_name, did).start();


    }//GEN-LAST:event_jButtonGraphActionPerformed
    public static int rowselected;
    private void jTableReadingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableReadingMouseClicked
        // TODO add your handling code here:
        //  jTableReading.setRowSelectionAllowed(true);


    }//GEN-LAST:event_jTableReadingMouseClicked

    private void jButtonReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReportsActionPerformed

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExportToCSV().setVisible(true);
            }
        });
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonReportsActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        //File file="/src/about.pdf"";
        //Uri uri = U ri.fromFile(file);
        File file;
        //file = System()
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new UserRegistation().setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        JdbcSQLiteConnection.shutdown();
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed

        new setPort().setVisible(true);

        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItemAddDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAddDeviceActionPerformed

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItemAddDeviceActionPerformed

    public class MyCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, java.lang.Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            java.awt.Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            cellComponent.setBackground(Color.GREEN);
            return cellComponent;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ActivatedScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ActivatedScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ActivatedScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActivatedScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ActivatedScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EUName;
    private javax.swing.JLabel PlanName;
    private javax.swing.JLabel ProName;
    private javax.swing.JLabel cname;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonGraph;
    private javax.swing.JButton jButtonReports;
    private javax.swing.JButton jButtonSettings;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItemAddDevice;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTableReading;
    private javax.swing.JTable jTableRelay;
    private javax.swing.JTextArea jTextAreaDeviceLogs;
    // End of variables declaration//GEN-END:variables

}
