package examples.com.intelligt.modbus.examples;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.Timer;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example to show how we can create a dynamic chart.
 */
public class DynamicLineAndTimeSeriesChart extends JFrame implements ActionListener {

    /**
     * The time series data.
     */
    private TimeSeries series1, series2;

    /**
     * The most recent value added.
     */
    private double lastValue = 1000.0;

    /**
     * Timer to refresh graph after every 1/4th of a second = 250
     */
    private Timer timer = new Timer(1000, this);
    public static String para = null, dev_name = null;
    public static int did = 1;

    /**
     * Constructs a new dynamic chart application.
     *
     * @param title the frame title.
     * @param paraname
     * @param value
     */
    public DynamicLineAndTimeSeriesChart(final String title, String paraname, String devName, int sid) {

        super(title);
        para = paraname;
        dev_name = devName;
        did = sid;
        this.series1 = new TimeSeries(paraname, Millisecond.class);
        // this.series2 = new TimeSeries("Paramater2", Millisecond.class);

        final TimeSeriesCollection dataset1 = new TimeSeriesCollection(this.series1);
        // final TimeSeriesCollection dataset2 = new TimeSeriesCollection(this.series2);
        final JFreeChart chart1 = createChart(dataset1, para);
        //final JFreeChart chart2 = createChart(dataset2);

        timer.setInitialDelay(1000);

        //Sets background color of chart
        chart1.setBackgroundPaint(Color.PINK);

        //Created JPanel to show graph on screen
        final JPanel content = new JPanel(new BorderLayout());

        //Created Chartpanel for chart area
        final ChartPanel chartPanel1 = new ChartPanel(chart1);
        //     final ChartPanel chartPanel2 = new ChartPanel(chart2);

        //Added chartpanel to main panel
        content.add(chartPanel1);
        //content.add(chartPanel2);

        //Sets the size of whole window (JPanel)
        chartPanel1.setPreferredSize(new java.awt.Dimension(800, 500));
        //   chartPanel2.setPreferredSize(new java.awt.Dimension(800, 200));

        //Puts the whole content on a Frame
        setContentPane(content);

        timer.start();

    }

    /**
     * Creates a sample chart.
     *
     * @param dataset the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset, String paraname) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
                "",
                "Time",
                paraname,
                dataset,
                true,
                true,
                false
        );
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        final XYPlot plot = result.getXYPlot();

        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.CYAN);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLUE);

        plot.setDataset(0, dataset);

        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);

        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(30000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);

        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(0.0, 1000.0);

        return result;
    }

    /**
     * Generates an random entry for a particular call made by time for every
     * 1/4th of a second.
     *
     * @param e the action event.
     */
    public void actionPerformed(final ActionEvent e) {

        final double factor = 0.9 + 0.2 * Math.random();
        this.lastValue = this.lastValue * factor;

        if ("LXT-330 Single Channel".equals(dev_name)) {
            final Millisecond now = new Millisecond();
            this.series1.add(new Millisecond(), ActivatedScreen.c1p1val[did]);
            //this.series2.add(new Millisecond(), ActivatedScreen.c2p2val[1]);

            System.out.println("Current Milliseconds = " + now.toString());
        } else {
            final Millisecond now = new Millisecond();
            this.series1.add(new Millisecond(), ActivatedScreen.c2p2val[did]);
            //this.series2.add(new Millisecond(), ActivatedScreen.c2p2val[1]);

            System.out.println("Current Milliseconds = " + now.toString());
        }

    }

    /**
     * Starting point for the dynamic graph application.
     *
     * @param paraname
     * @param value
     * @param args ignored.
     */
    // public static void main(final String[] args) 
    // {
    public static void drawGraph(String paraname, String devName, int sid) {

        final DynamicLineAndTimeSeriesChart demo = new DynamicLineAndTimeSeriesChart("Realtime Graph", paraname, devName, sid);
        demo.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
