package springmvc.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import ru.cuefinder.CueFinder;
import ru.cuefinder.CueFinderFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Calls selected mp3 file splitting
 */
public class SplitFileController implements Controller
{

    public ModelAndView handleRequest( HttpServletRequest request,
                                       HttpServletResponse response )
            throws Exception
    {

        String selectedFile = request.getParameter( "fn" );

        CueFinder cf = CueFinderFactory.makeCueFinder( selectedFile );
        cf.process();
        ModelAndView modelAndView = new ModelAndView( "split_result" );
        modelAndView.addObject( "cf", cf );


        return modelAndView;
    }
}