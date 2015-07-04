/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.parser.parboiled;

import cz.miroslavbartyzal.psdiagram.app.global.SettingsHolder;
import javax.swing.SwingUtilities;
import org.parboiled.Rule;
import org.parboiled.buffers.CharSequenceInputBuffer;
import org.parboiled.buffers.InputBuffer;
import org.parboiled.errors.ParseError;
import org.parboiled.errors.ParserRuntimeException;
import org.parboiled.parserunners.BasicParseRunner;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.parserunners.StoppableRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 * @param <V>
 */
public class ParboiledParseRunner<V> implements StoppableRunner
{

    private StopableParser parser = null;

    public void run(String input, Rule rule, ParboiledRecoveryListener<V> recoveryListener)
    {
        InputBuffer inputBuffer = new CharSequenceInputBuffer(input);
        stopParsing();
        parser = new StopableParser(input, inputBuffer, rule, 5000, recoveryListener);
        parser.start();
    }

    @Override
    public void stopParsing()
    {
        if (parser != null && parser.isAlive()) {
            parser.stopRecovery();
        }
    }

    private class StopableParser extends Thread
    {

        private volatile boolean stopped = false;

        private final String input;
        private final InputBuffer inputBuffer;
        private final Rule rule;
        private final long timeout;
        private final ParboiledRecoveryListener<V> recoveryListener;
        private volatile StoppableRunner runner;

        public StopableParser(String input, InputBuffer inputBuffer, Rule rule, long timeout,
                ParboiledRecoveryListener<V> recoveryListener)
        {
            this.input = input;
            this.inputBuffer = inputBuffer;
            this.rule = rule;
            this.timeout = timeout;
            this.recoveryListener = recoveryListener;
        }

        @Override
        public void run()
        {
            if (stopped) {
                return;
            }

            ParsingResult<V> recoveryResult = null;
            ParsingResult<V> basicResult = null;
            final RecoveringParseRunner<V> recoveryRunner = new RecoveringParseRunner<>(rule,
                    timeout);

            try {
                BasicParseRunner<V> basicRunner = new BasicParseRunner<>(rule);
                runner = basicRunner;
                basicResult = basicRunner.run(inputBuffer);
                if (stopped) {
                    return;
                }

                if (SettingsHolder.IS_DEVELOPMENT_RUN_MODE) {
                    System.out.println("basicResult: " + ParseTreeUtils.printNodeTree(basicResult));
                }

                final ParsingResult<V> parseResult = basicResult;
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        recoveryListener.onValidationComplete(
                                parseResult.matched && !parseResult.hasCollectedParseErrors());
                        if (parseResult.matched && parseResult.hasCollectedParseErrors()) {
                            recoveryListener.onRecoverySuccess(parseResult, input);
                        }
                    }
                });
                if (basicResult.matched) {
                    return;
                }

                runner = recoveryRunner;
                recoveryResult = recoveryRunner.run(inputBuffer, basicResult);

                if (SettingsHolder.IS_DEVELOPMENT_RUN_MODE) {
                    System.out.println("recoveryResult: " + ParseTreeUtils.printNodeTree(
                            recoveryResult));
                }
            } catch (RecoveringParseRunner.TimeoutException | ParserRuntimeException ex) {
//                System.out.println(
//                        "INFO: returned prematurely from parsing input '" + input + "'.");
                if (!(ex instanceof RecoveringParseRunner.TimeoutException)
                        && (ex.getCause() == null || !(ex.getCause() instanceof RecoveringParseRunner.TimeoutException))) {
                    ex.printStackTrace(System.err);
                }
            }

            if (!stopped) {
                final ParsingResult<V> parseResult = recoveryResult;
                final ParsingResult<V> parseResultBasic = basicResult;

                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (parseResult == null) {
                            ParsingResult<V> artificialResult = new ParsingResult<>(
                                    recoveryRunner.getParseErrors().isEmpty(), null,
                                    recoveryRunner.getValueStack(), recoveryRunner.getParseErrors(),
                                    inputBuffer);
                            if (parseResultBasic != null) {
                                // include my errors if not present already
                                parentLoop:
                                for (ParseError parseError : parseResultBasic.parseErrors) {
                                    if (parseError instanceof ParboiledSyntaxError) {
                                        int index = 0;
                                        for (int i = 0; i < artificialResult.parseErrors.size(); i++) {
                                            ParseError error = artificialResult.parseErrors.get(i);
                                            if (error.getStartIndex() == parseError.getStartIndex()
                                                    && error.getEndIndex() == parseError.getEndIndex()
                                                    && error instanceof ParboiledSyntaxError
                                                    && error.getErrorMessage().equals(
                                                            parseError.getErrorMessage())) {
                                                // error is already present
                                                continue parentLoop;
                                            } else if (error.getStartIndex() < parseError.getStartIndex()) {
                                                index = i + 1;
                                            }
                                        }
                                        // error is not present
                                        artificialResult.parseErrors.add(index, parseError);
                                    }
                                }
                            }
                            recoveryListener.onRecoveryException(artificialResult, input);
                        } else {
                            recoveryListener.onRecoverySuccess(parseResult, input);
                        }
                    }
                });
            }

            stopped = true;
        }

        public void stopRecovery()
        {
            stopped = true;
            if (runner != null) {
                runner.stopParsing();
            }
        }

    }

}
