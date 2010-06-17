<%@ page import="java.io.File" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%

    response.setContentType( "text/html; charset=UTF-8" );
    String descParam = request.getParameter( "desc" );
    String desc = ( descParam == null || !"yes".equals( descParam ) ) ? "yes" : "no";
    String fn = request.getParameter( "fn" );
    String upDir = fn == null ? null : new java.io.File( fn ).getParent();

%>
<html>
<body>
	<p>This is file list:</p>
    <a href="file_list.html?fn=<%=upDir%>&sort=${param.sort}&desc=<%= desc%>">..</a>
 <table border="1">
     <tr>
         <td align="center"><a href="file_list.html?fn=${param.fn}&sort=modDate&desc=<%= desc%>">Modification date </a></td>
         <td align="center"><a href="file_list.html?fn=${param.fn}&sort=name&desc=<%= desc%>">name </a></td>
         <td align="center">Size</td>
         <td align="center"> Action </td>
     </tr>
      <c:forEach var="fItem" items="${file_list}">
        <tr>
           <td><fmt:formatDate value="${fItem.lastModified}" type="date" dateStyle="long" /></td>

<c:choose>
  <c:when test="${fItem.directory}" >

      <td><a href="file_list.html?fn=${fItem.file.absolutePath}">${fItem.file.name}</a></td>
      <td> - </td>
      <td> - </td>
  </c:when>
  <c:otherwise>
      <td> ${fItem.file.name}</td>
      <td> ${fItem.length} </td>
      <td> <a href="process_file.html?fn=${fItem.file.absolutePath}">Process</a></td>
  </c:otherwise>
</c:choose>
</tr>
      </c:forEach>
</table>
</body>
</html>
