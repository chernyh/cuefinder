package springmvc.domain;

import java.io.File;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chernyh
 * Date: 11.06.2010
 * Time: 1:05:53
 * To change this template use File | Settings | File Templates.
 */
public class FileItem
{
    private File file;

    private long length;
    private boolean isDirectory;

    public File getFile()
    {
        return file;
    }

    public void setFile( File file )
    {
        this.file = file;
    }

    public long getLength()
    {
        return file.length();
    }

    public void setLength( long length )
    {
        this.length = length;
    }

    public boolean isDirectory()
    {
        return file.isDirectory();
    }

    public void setDirectory( boolean directory )
    {
        isDirectory = directory;
    }

    public Date getLastModified()
    {
        return new Date( file.lastModified() );
    }
}
