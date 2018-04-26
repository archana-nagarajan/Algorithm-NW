import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.awt.BasicStroke; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.XYSeries; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RefineryUtilities; 
import org.jfree.chart.plot.XYPlot; 
import org.jfree.chart.ChartFactory; 
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.data.xy.XYSeriesCollection; 
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class GraphGenerator extends ApplicationFrame {
   public GraphGenerator( String applicationTitle, String chartTitle, HashMap<Integer, Integer> resultGraph )
   {
      super(applicationTitle);
      JFreeChart xylineChart = ChartFactory.createXYLineChart(
         chartTitle ,"Actual" ,"Estimated" ,createDataset(resultGraph) ,
         PlotOrientation.VERTICAL ,
         true , true , false);
         
      ChartPanel chartPanel = new ChartPanel(xylineChart);
      chartPanel.setPreferredSize( new java.awt.Dimension(560 , 367));
      final XYPlot plot = xylineChart.getXYPlot();
      XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
      renderer.setSeriesPaint( 0 , Color.RED );
      renderer.setSeriesPaint( 1 , Color.GREEN );
      renderer.setSeriesStroke( 0 , new BasicStroke( 1.0f ) );
      renderer.setSeriesStroke( 1 , new BasicStroke( 1.0f ) );
      plot.setRenderer( renderer ); 
      setContentPane( chartPanel ); 
   }
   
   private XYDataset createDataset(HashMap<Integer, Integer> resultGraph){
      final XYSeries firefox = new XYSeries( "Flow Cardinality (Actual vs Estimated)" ); 
      final XYSeries firefox1 = new XYSeries("Linear plot");
      for(Map.Entry<Integer, Integer> entry : resultGraph.entrySet()){  
			firefox.add( entry.getKey() , entry.getValue());
			firefox1.add( entry.getKey() , entry.getKey());
      }     
      final XYSeriesCollection dataset = new XYSeriesCollection();          
      dataset.addSeries( firefox );
      dataset.addSeries( firefox1 );
      return dataset;
   }
}