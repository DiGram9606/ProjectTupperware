package src.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import src.util.PlotFunction;

public class GraphPanel extends JPanel {
   private List<PlotFunction> functions;
   private double xMin = -10.0;
   private double xMax = 10.0;
   private double yMin = -10.0;
   private double yMax = 10.0;

   public void setFunctions(List<PlotFunction> var1) {
      this.functions = var1;
   }

   public void setLimits(double var1, double var3, double var5, double var7) {
      this.xMin = var1;
      this.xMax = var3;
      this.yMin = var5;
      this.yMax = var7;
      this.repaint();
   }

   public GraphPanel() {
      this.setBackground(Color.BLACK);
   }

   protected void paintComponent(Graphics var1) {
      super.paintComponent(var1);
      Graphics2D var2 = (Graphics2D)var1;
      var2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      int var3 = this.getWidth();
      int var4 = this.getHeight();
      double var5 = (double)var3 / (this.xMax - this.xMin);
      double var7 = (double)var4 / (this.yMax - this.yMin);
      this.drawGrid(var2, var3, var4, var5, var7);
      this.drawAxes(var2, var3, var4, var5, var7);
      this.drawLabels(var2, var3, var4, var5, var7);
      if (this.functions != null) {
         Iterator var9 = this.functions.iterator();

         while(var9.hasNext()) {
            PlotFunction var10 = (PlotFunction)var9.next();
            this.drawFunction(var2, var10, var3, var4, var5, var7);
         }
      }

      this.drawLimitsInfo(var2);
   }

   private void drawGrid(Graphics2D var1, int var2, int var3, double var4, double var6) {
      var1.setColor(new Color(60, 60, 60));
      var1.setStroke(new BasicStroke(0.5F));

      double var8;
      int var10;
      for(var8 = Math.ceil(this.xMin); var8 <= Math.floor(this.xMax); ++var8) {
         var10 = (int)((var8 - this.xMin) * var4);
         var1.drawLine(var10, 0, var10, var3);
      }

      for(var8 = Math.ceil(this.yMin); var8 <= Math.floor(this.yMax); ++var8) {
         var10 = var3 - (int)((var8 - this.yMin) * var6);
         var1.drawLine(0, var10, var2, var10);
      }

   }

   private void drawAxes(Graphics2D var1, int var2, int var3, double var4, double var6) {
      var1.setColor(Color.WHITE);
      var1.setStroke(new BasicStroke(2.0F));
      int var8 = var3 - (int)((0.0 - this.yMin) * var6);
      if (var8 >= 0 && var8 <= var3) {
         var1.drawLine(0, var8, var2, var8);
      }

      int var9 = (int)((0.0 - this.xMin) * var4);
      if (var9 >= 0 && var9 <= var2) {
         var1.drawLine(var9, 0, var9, var3);
      }

   }

   private void drawLabels(Graphics2D var1, int var2, int var3, double var4, double var6) {
      var1.setColor(Color.LIGHT_GRAY);
      var1.setFont(new Font("Arial", 0, 12));
      int var8 = var3 - (int)((0.0 - this.yMin) * var6);
      int var9 = (int)((0.0 - this.xMin) * var4);

      double var10;
      int var12;
      String var13;
      for(var10 = Math.ceil(this.xMin); var10 <= Math.floor(this.xMax); ++var10) {
         if (!(Math.abs(var10) < 0.001)) {
            var12 = (int)((var10 - this.xMin) * var4);
            var13 = String.format("%.0f", var10);
            var1.drawString(var13, var12 - 5, Math.min(var8 + 18, var3 - 5));
         }
      }

      for(var10 = Math.ceil(this.yMin); var10 <= Math.floor(this.yMax); ++var10) {
         if (!(Math.abs(var10) < 0.001)) {
            var12 = var3 - (int)((var10 - this.yMin) * var6);
            var13 = String.format("%.0f", var10);
            var1.drawString(var13, Math.max(var9 + 8, 5), var12 + 4);
         }
      }

      if (this.xMin < 0.0 && this.xMax > 0.0 && this.yMin < 0.0 && this.yMax > 0.0) {
         var1.setColor(Color.WHITE);
         var1.drawString("0", var9 + 8, var8 + 18);
      }

   }

   private void drawFunction(Graphics2D var1, PlotFunction var2, int var3, int var4, double var5, double var7) {
      var1.setColor(var2.getColor());
      var1.setStroke(new BasicStroke(2.0F));
      Path2D.Double var9 = new Path2D.Double();
      boolean var10 = true;
      double var11 = (this.xMax - this.xMin) / (double)var3;

      for(double var13 = this.xMin; var13 <= this.xMax; var13 += var11) {
         double var15 = (Double)var2.getFunction().apply(var13);
         if (Double.isFinite(var15) && var15 >= this.yMin && var15 <= this.yMax) {
            int var17 = (int)((var13 - this.xMin) * var5);
            int var18 = var4 - (int)((var15 - this.yMin) * var7);
            if (var10) {
               var9.moveTo((double)var17, (double)var18);
               var10 = false;
            } else {
               var9.lineTo((double)var17, (double)var18);
            }
         } else {
            var10 = true;
         }
      }

      var1.draw(var9);
   }

   private void drawLimitsInfo(Graphics2D var1) {
      var1.setColor(Color.CYAN);
      var1.setFont(new Font("Arial", 0, 12));
      String var2 = String.format("Limits: X[%.1f, %.1f] Y[%.1f, %.1f]", this.xMin, this.xMax, this.yMin, this.yMax);
      var1.drawString(var2, 10, this.getHeight() - 10);
   }
}
