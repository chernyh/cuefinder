package springmvc.web;

import java.io.FileFilter;
import java.io.IOException;
import java.io.File;
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

        ModelAndView modelAndView = new ModelAndView( "file_list" );
        modelAndView.addObject( "file_list", files );

        return modelAndView;
    }
}
