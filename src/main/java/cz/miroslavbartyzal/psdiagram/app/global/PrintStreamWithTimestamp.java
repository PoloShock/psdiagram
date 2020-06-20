/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.miroslavbartyzal.psdiagram.app.global;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;

/*
 * Quick and dirty hack to have timestamps in log file.
 * Much better would be to use some logging framework but this is less time consuming to hook up.
 *
 * @author Miroslav Bartyzal (miroslavbartyzal@gmail.com)
 */
public class PrintStreamWithTimestamp extends PrintStream
{

    public PrintStreamWithTimestamp(OutputStream out)
    {
        super(out);
    }

    public PrintStreamWithTimestamp(OutputStream out, boolean autoFlush)
    {
        super(out, autoFlush);
    }

    public PrintStreamWithTimestamp(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException
    {
        super(out, autoFlush, encoding);
    }

    public PrintStreamWithTimestamp(String fileName) throws FileNotFoundException
    {
        super(fileName);
    }

    public PrintStreamWithTimestamp(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException
    {
        super(fileName, csn);
    }

    public PrintStreamWithTimestamp(File file) throws FileNotFoundException
    {
        super(file);
    }

    public PrintStreamWithTimestamp(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException
    {
        super(file, csn);
    }

    @Override
    public void println(String s)
    {
        super.println(addTimeStamp(s));
    }

    private String addTimeStamp(String s)
    {
        return new Timestamp(new Date().getTime()).toString() + " " + s;
    }

}
