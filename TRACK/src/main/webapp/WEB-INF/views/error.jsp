<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Erreur" scope="request"/>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<c:set var="code" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<main class="error-page">
    <a class="brand" href="${pageContext.request.contextPath}/login"><span class="brand-mark">T</span><span>TRACK</span></a>
    <c:choose>
        <c:when test="${code == 403}"><h1>Accès refusé</h1><p>Vous n'avez pas le droit d'effectuer cette action, ou votre session a expiré.</p></c:when>
        <c:when test="${code == 404}"><h1>Page introuvable</h1><p>Cette page n'existe pas ou n'existe plus.</p></c:when>
        <c:otherwise><h1>Une erreur est survenue</h1><p>Réessayez dans un instant. Si le problème continue, contactez l'administrateur.</p></c:otherwise>
    </c:choose>
    <a class="btn btn-track" href="${pageContext.request.contextPath}/dashboard">Retour à l'application</a>
</main>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
