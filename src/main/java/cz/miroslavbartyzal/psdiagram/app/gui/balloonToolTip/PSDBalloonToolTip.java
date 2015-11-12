/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PSDBalloonToolTip
{

    private final BalloonTipStyle balloonTipStyle;
    private final BalloonTip balloonTip;
    private final JPanel balloonTipContent;
    private final FadeOutTimer fadeOutTimer;
    private static final int ARC_SIZE = 5;
    private int lastInitialDelay = -1;
    private boolean displayMessages = false;
    private final JPanel balloonTipTempContent;
    private final JLabel balloonTipTempMessage;
    private final JComponent parentComponent;
    private final MaxBalloonSizeCallback maxBalloonSizeCallback;

    public PSDBalloonToolTip(JComponent parentComponent,
            MaxBalloonSizeCallback maxBalloonSizeCallback)
    {
        this.parentComponent = parentComponent;
        this.maxBalloonSizeCallback = maxBalloonSizeCallback;
        JLabel balloonTipMessage = new JLabel();
        JLabel balloonTipCommand = new JLabel();
        balloonTipMessage.setFont(SettingsHolder.CODEFONT);
        balloonTipCommand.setFont(SettingsHolder.CODEFONT);
        balloonTipContent = new JPanel();
        balloonTipContent.setLayout(new BoxLayout(balloonTipContent, BoxLayout.Y_AXIS));
        balloonTipContent.setOpaque(false);
        balloonTipContent.add(balloonTipMessage);
        balloonTipContent.add(balloonTipCommand);

        balloonTipTempMessage = new JLabel();
        balloonTipTempMessage.setFont(SettingsHolder.CODEFONT);
        balloonTipTempContent = new JPanel();
        balloonTipTempContent.setLayout(new BoxLayout(balloonTipTempContent, BoxLayout.Y_AXIS));
        balloonTipTempContent.setOpaque(false);
        balloonTipTempContent.add(balloonTipTempMessage);

        balloonTipStyle = new RoundedBalloonStyle(ARC_SIZE, ARC_SIZE, Color.WHITE, Color.BLACK);
        balloonTip = new BalloonTip(parentComponent, balloonTipContent, balloonTipStyle, false)
        {
            @Override
            public Dimension getPreferredSize()
            {
                Dimension dimension = new Dimension(super.getPreferredSize());
                Dimension dimMax = PSDBalloonToolTip.this.maxBalloonSizeCallback.getMaxBalloonSize();
                if (dimension.width > dimMax.width) {
                    dimension.width = dimMax.width;
                }
                if (dimension.height > dimMax.height) {
                    dimension.height = dimMax.height;
                }
                return dimension;
            }

            @Override
            public boolean contains(Point p)
            {
                /*
                 * Here we have to verify that the point is inside the tooltip rectangle,
                 * not just somewhere below it where the lead triangle is.
                 */
                return super.contains(p)
                        && p.getY() <= super.getHeight() - balloonTipStyle.getMinimalHorizontalOffset() + ARC_SIZE; // the ARC_SIZE is there just because RoundedBalloonStyle returns bad offset
            }

            @Override
            public boolean contains(int x, int y)
            {
                /*
                 * Here we have to verify that the point is inside the tooltip rectangle,
                 * not just somewhere below it where the lead triangle is.
                 */
                return super.contains(x, y)
                        && y <= super.getHeight() - balloonTipStyle.getMinimalHorizontalOffset() + ARC_SIZE; // the ARC_SIZE is there just because RoundedBalloonStyle returns bad offset
            }

        };
        balloonTip.setVisible(false);

        MyMouseMotionListener myMouseMotionListener = new MyMouseMotionListener();
        balloonTip.addMouseListener(myMouseMotionListener);
        balloonTip.addMouseMotionListener(myMouseMotionListener);

        fadeOutTimer = new FadeOutTimer(500, 3000, 25);

        parentComponent.addFocusListener(new FocusListener()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                fadeOutTimer.dismiss();
            }

            @Override
            public void focusGained(FocusEvent e)
            {
                if (displayMessages) {
                    fadeOutTimer.stopFadingOut();
                    balloonTip.setContents(balloonTipContent);
                    balloonTip.setVisible(true);

                    if (lastInitialDelay >= 0) {
                        fadeOutTimer.setInitialDelay(lastInitialDelay);
                        if (balloonTip.getMousePosition() == null) {
                            // mouse pointer is not inside of the balloon tool tip
                            fadeOutTimer.startFadingOut();
                        }
                        fadeOutTimer.setInitialDelay(100); // for the cases after the first fading out
                    }
                }
            }
        });
    }

    public void wipeMessage()
    {
        lastInitialDelay = -1;
        displayMessages = false;
        dismiss();
    }

    /**
     * Shows messages from by top to bottom layout for given milliseconds.
     * <p>
     * @param messages
     * @param timeMilliseconds can be negative number - in that case messages never disappear on their own
     */
    public void showMessages(List<String> messages, int timeMilliseconds)
    {
        displayMessages = true;
        lastInitialDelay = timeMilliseconds;

        fadeOutTimer.stopFadingOut();
        balloonTipContent.removeAll();
        for (String message : messages) {
            JLabel jLabel = new JLabel(message);
            jLabel.setFont(SettingsHolder.CODEFONT);
            balloonTipContent.add(jLabel);
        }
        balloonTip.setContents(balloonTipContent);
        if (parentComponent.hasFocus()) {
            balloonTip.setVisible(true);
            if (lastInitialDelay >= 0) {
                fadeOutTimer.setInitialDelay(timeMilliseconds);
                if (balloonTip.getMousePosition() == null) {
                    // mouse pointer is not inside of the balloon tool tip
                    fadeOutTimer.startFadingOut();
                }
                fadeOutTimer.setInitialDelay(100); // for the cases after the first fading out
            }
        }
    }

    /**
     * Shows message that won't ever pop up again after it's dismission.
     * Instead, the previous one will pop up if present.
     * <p>
     * @param message
     * @param timeMilliseconds
     */
    public void showTemporaryMessage(String message, int timeMilliseconds)
    {
        fadeOutTimer.setInitialDelay(timeMilliseconds);
        fadeOutTimer.stopFadingOut();

        balloonTipTempMessage.setText(message);
        balloonTip.setContents(balloonTipTempContent);
        balloonTip.setVisible(true);

        if (balloonTip.getMousePosition() == null) {
            // mouse pointer is not inside of the balloon tool tip
            fadeOutTimer.startFadingOut();
        }
        fadeOutTimer.setInitialDelay(100); // for the cases after the first fading out
    }

    public void dismiss()
    {
        fadeOutTimer.dismiss();
    }

    private class MyMouseMotionListener extends MouseAdapter
    {

        @Override
        public void mouseMoved(MouseEvent e)
        {
            if (lastInitialDelay >= 0) {
                fadeOutTimer.stopFadingOut();
            }
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            if (lastInitialDelay >= 0) {
                fadeOutTimer.startFadingOut();
            }
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            balloonTip.setVisible(false);
            fadeOutTimer.stopFadingOut();
        }

    }

    private class FadeOutTimer
    {

        private final Timer timer;
        private int curTime;

        public FadeOutTimer(int initialDelay, final int fadeoutTime, int refreshRate)
        {
            final int timeDelta = 1000 / refreshRate;
            timer = new Timer(timeDelta, new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    curTime += timeDelta;
                    float newOpacity = (-1.0f / fadeoutTime) * curTime + 1.0f; // f(time)=(-1/time)*curTime+1
                    if (newOpacity <= 0.0f || Float.isNaN(newOpacity)) {
                        dismiss();
                    } else {
                        balloonTip.setOpacity(newOpacity);
                    }
                }
            });
            timer.setInitialDelay(initialDelay);
            timer.setRepeats(true);
        }

        public void dismiss()
        {
            timer.stop();
            balloonTip.setVisible(false);
            balloonTip.setOpacity(1.0f);
        }

        public void startFadingOut()
        {
            if (!timer.isRunning()) {
                balloonTip.setOpacity(1.0f);
                curTime = 0;
                timer.start();
            }
        }

        public void stopFadingOut()
        {
            if (timer.isRunning()) {
                timer.stop();
                balloonTip.setOpacity(1.0f);
            }
        }

        public void setInitialDelay(int initialDelay)
        {
            timer.setInitialDelay(initialDelay);
        }

    }

}
