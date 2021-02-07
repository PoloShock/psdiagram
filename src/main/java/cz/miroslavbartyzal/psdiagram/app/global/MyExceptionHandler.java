/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global;

/**
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class MyExceptionHandler implements Thread.UncaughtExceptionHandler
{

    public static void handle(Throwable e)
    {
        handle(Thread.currentThread(), e);
    }

    @Override
    public final void uncaughtException(Thread t, Throwable e)
    {
        handle(t, e);
    }

    private synchronized static void handle(Thread t, Throwable e)
    {
        System.err.println("Exception in thread \"" + t.getName() + "\" ");
        e.printStackTrace(System.err);
    }

}
