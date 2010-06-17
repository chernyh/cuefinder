package springmvc.web;

import java.io.FileFilter;
import java.io.IOException;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import springmvc.domain.FileItem;

public class FileListController implements Controller
{

    //public static final String HOME_DIR = "/home/chernyh";
    public static final String HOME_DIR = "/mnt/multimedia/_RADIOSHOW_";

    private class _50MBFilter implements FileFilter
    {
        public boolean accept( File file )
        {
            return file.isDirectory() || ( file.length() > 50000000 );//50MB minimum
        }
    }

    enum SortingMode
    {
        name,
        modDate
    }

    private class FileComparator implements Comparator
    {
        private SortingMode sm;
        private boolean sortAscending;

        public FileComparator( SortingMode sm, boolean sortAscending )
        {
            this.sm = sm;
            this.sortAscending = sortAscending;
        }

        private int result;

        public int compare( Object o1, Object o2 )
        {
            FileItem w1 = ( FileItem ) o1;
            FileItem w2 = ( FileItem ) o2;
            switch( sm )
            {
                case name:
                    result = w1.getFile().getName().compareTo( w2.getFile().getName() );
                    break;
                case modDate:
                    result = Long.valueOf( w1.getFile().lastModified() ).compareTo( Long.valueOf( w2.getFile().lastModified() ) );
                    break;
                default:
                    result = 0;

            }
            if( !sortAscending )
                result *= -1;
            return result;
        }
    }


    public ModelAndView handleRequest( HttpServletRequest request,
                                       HttpServletResponse response ) throws ServletException, IOException
    {


        String requestedPath = request.getParameter( "fn" );
        requestedPath = requestedPath == null ? HOME_DIR : requestedPath;

        File selectedDir = new File( requestedPath );
        List files = new ArrayList();
        for( File fn : selectedDir.listFiles( new _50MBFilter() ) )
        {
            FileItem fi = new FileItem();
            fi.setFile( fn );
            files.add( fi );
        }

        SortingMode sm = SortingMode.modDate;
        try
        {
            sm = SortingMode.valueOf( request.getParameter( "fn" ) );
        }
        catch( IllegalArgumentException e )
        {
            e.printStackTrace();
        }

        String desc = request.getParameter( "desc" );
        boolean ascending = desc != null && !"yes".equals( desc );
        Collections.sort( files, new FileComparator( sm, ascending ) );

        ModelAndView modelAndView = new ModelAndView( "file_list" );
        modelAndView.addObject( "file_list", files );

        return modelAndView;
    }

}
