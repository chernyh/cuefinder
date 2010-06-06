<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
	<p>This is file list:</p>
 <table>
      <c:forEach var="file" items="${file_list}">
        <tr>
<td>
<c:choose> 
  <c:when test="${file.directory}" > 
      <a href="file_list.html?fn=${file.name}">${file.name}</a>
  </c:when>
  <c:otherwise>
  <c:out value="${file.name}"/> 
  </c:otherwise> 
</c:choose> 
</td>
</tr>
      </c:forEach>
</table>
</body>
</html>
