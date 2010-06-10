<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
	<p>This is file list:</p>
 <table>
      <c:forEach var="fItem" items="${file_list}">
        <tr>
<td>
<c:choose> 
  <c:when test="${fItem.directory}" >
      <a href="file_list.html?fn=${fItem.file.absolutePath}">${fItem.file.name}</a>
  </c:when>
  <c:otherwise>
  ${fItem.length}  ${fItem.file.name} <a href="process_file.html?fn=${fItem.file.absolutePath}">Process</a> 
  </c:otherwise> 
</c:choose> 
</td>
</tr>
      </c:forEach>
</table>
</body>
</html>
