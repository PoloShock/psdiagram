/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.gui.balloonToolTip;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import java.awt.Color;
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
    private final JPanel balloonTipTempContent;
    private final JLabel balloonTipTempMessage;
    private final JComponent parentComponent;

    public PSDBalloonToolTip(JComponent parentComponent)
    {
        this.parentComponent = parentComponent;
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
        balloonTip = new BalloonTip(parentComponent, balloonTipContent, balloonTipStyle, false);
        balloonTip.setVisible(false);

        MyMouseMotionListener myMouseMotionListener = new MyMouseMotionListener();
        balloonTip.addMouseListener(myMouseMotionListener);
        balloonTip.addMouseMotionListener(myMouseMotionListener);

        fadeOutTimer = new FadeOutTimer(500, 2000, 25);

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
                if (lastInitialDelay >= 0) {
                    fadeOutTimer.setInitialDelay(lastInitialDelay);
                    fadeOutTimer.stopFadingOut();
                    balloonTip.setContents(balloonTipContent);
                    balloonTip.setVisible(true);

                    if (balloonTip.getMousePosition() == null || balloonTip.getMousePosition().y > balloonTip.getHeight() - balloonTipStyle.getMinimalHorizontalOffset() + ARC_SIZE) {
                        // mouse pointer is not inside of the balloon tool tip
                        fadeOutTimer.startFadingOut();
                    }
                    fadeOutTimer.setInitialDelay(100); // for the cases after the first fading out
                }
            }
        });
    }

    public void wipeMessage()
    {
        lastInitialDelay = -1;
        dismiss();
    }

    public void showMessages(List<String> messages, int initialDelay)
    {
        lastInitialDelay = initialDelay;

        fadeOutTimer.setInitialDelay(initialDelay);
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

            if (balloonTip.getMousePosition() == null || balloonTip.getMousePosition().y > balloonTip.getHeight() - balloonTipStyle.getMinimalHorizontalOffset() + ARC_SIZE) {
                // mouse pointer is not inside of the balloon tool tip
                fadeOutTimer.startFadingOut();
            }
        }
        fadeOutTimer.setInitialDelay(100); // for the cases after the first fading out
    }

    /**
     * Shows message that won't ever pop up again after it's dismission.
     * Instead, the previous one will pop up if present.
     * <p>
     * @param message
     * @param initialDelay
     */
    public void showTemporaryMessage(String message, int initialDelay)
    {
        fadeOutTimer.setInitialDelay(initialDelay);
        fadeOutTimer.stopFadingOut();

        balloonTipTempMessage.setText(message);
        balloonTip.setContents(balloonTipTempContent);
        balloonTip.setVisible(true);

        if (balloonTip.getMousePosition() == null || balloonTip.getMousePosition().y > balloonTip.getHeight() - balloonTipStyle.getMinimalHorizontalOffset() + ARC_SIZE) {
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
            if (e.getY() <= e.getComponent().getHeight() - balloonTipStyle.getMinimalHorizontalOffset() + ARC_SIZE) { // the ARC_SIZE is there just because RoundedBalloonStyle returns bad offset
                fadeOutTimer.stopFadingOut();
            } else {
                fadeOutTimer.startFadingOut();
            }
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            fadeOutTimer.startFadingOut();
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            if (e.getY() <= e.getComponent().getHeight() - balloonTipStyle.getMinimalHorizontalOffset() + ARC_SIZE) { // the ARC_SIZE is there just because RoundedBalloonStyle returns bad offset
                balloonTip.setVisible(false);
                fadeOutTimer.stopFadingOut();
            }
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
