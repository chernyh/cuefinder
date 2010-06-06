package springmvc.web;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class FileListController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

String homeDirPath ="/home/chernyh";
String requestedPath= request.getParameter("fn");
String resultPath= homeDirPath + (requestedPath==null?"":"/"+requestedPath);

		File homeDir = new File(resultPath);
		List files = new ArrayList();
		for(File fn: homeDir.listFiles() )
		{
		    files.add(fn);
		}
		
		ModelAndView modelAndView = new ModelAndView("file_list");
		modelAndView.addObject("file_list", files);
		
		return modelAndView;
	}
}
