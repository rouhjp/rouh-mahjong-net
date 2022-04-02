package jp.rouh.mahjong.app;

import javax.swing.*;
import java.awt.*;

class AutoResizablePanel extends JPanel{
    private final int baseWidth;
    private final int baseHeight;
    private final ApplicationContext context;
    AutoResizablePanel(int baseWidth, int baseHeight, ApplicationContext context){
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        this.context = context;
    }

    @Override
    public Dimension getPreferredSize(){
        int weight = context.getSizeWeight();
        return new Dimension(baseWidth*weight, baseHeight*weight);
    }
}
