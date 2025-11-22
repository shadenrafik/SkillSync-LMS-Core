package gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ChartPanel extends JPanel {
    private Map<String,Double>data;
    private String chartTitle;
    public ChartPanel(Map<String,Double>data,String chartTitle){
        this.data=data;
        this.chartTitle=chartTitle;
        setPreferredSize(new Dimension(700,250));
        setBackground(Color.WHITE);
    }
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        FontMetrics fm=g.getFontMetrics();
        int titleWidth=fm.stringWidth(chartTitle);
        g.drawString(chartTitle,(getWidth()-titleWidth)/2,20);
        if (data==null||data.isEmpty())return;
        int panelWidth=getWidth();
        int panelHeight=getHeight();
        int bottom=panelHeight-50;
        int maxBarHeight=panelHeight-100;
        double maxValue=data.values().stream().mapToDouble(v->v).max().orElse(1);

        int x=60;
        int barWidth=50;
        int spacing=(panelWidth-100)/Math.max(1,data.size());

        for (Map.Entry<String,Double>entry:data.entrySet()){
            double value=entry.getValue();
            int barHeight=(int) ((value/maxValue)*maxBarHeight);

            g.setColor(Color.PINK);
            g.fillRect(x,bottom-barHeight,barWidth,barHeight);

            g.setColor(Color.BLUE);
            int labelWidth=g.getFontMetrics().stringWidth(entry.getKey());
            g.drawString(entry.getKey(),x+(barWidth-labelWidth)/2,bottom+15);
            g.drawString(String.format("%.1f",value),x,bottom-barHeight-5);
            x+=spacing;
        }
    }

}