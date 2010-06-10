<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
<p>This is result page</p>
File processed: <c:out value="${cf.mp3Filename}"/><br/>

<c:forEach var="item" items="${cf.output}">
    <c:out value="${item}"/><br/>
</c:forEach>

</body>
</html>
