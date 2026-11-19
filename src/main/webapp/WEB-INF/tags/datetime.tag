<%@ tag body-content="empty" pageEncoding="UTF-8" description="Affiche une date/heure ISO au format 31/10/2026 14:28" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ attribute name="value" required="false" rtexprvalue="true" type="java.lang.Object" %>
<c:if test="${not empty value}"><c:set var="iso"><c:out value="${value}"/></c:set><time datetime="${iso}">${fn:substring(iso,8,10)}/${fn:substring(iso,5,7)}/${fn:substring(iso,0,4)} ${fn:substring(iso,11,16)}</time></c:if>
