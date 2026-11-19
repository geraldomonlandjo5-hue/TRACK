<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Suivi de colis" scope="request"/>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<header class="track-band">
    <div class="track-wrap">
        <a class="brand" href="${pageContext.request.contextPath}/track"><span class="brand-mark">T</span><span>TRACK</span></a>
        <h1>Suivez votre colis</h1>
        <p>Saisissez votre numéro de suivi pour consulter la progression.</p>
        <form class="tracking-search" method="get" action="${pageContext.request.contextPath}/track">
            <input class="form-control" name="number" aria-label="Numéro de suivi" placeholder="Ex. TRK-2026-1A2B3C4D5E6F" required maxlength="40" value="<c:out value='${param.number}'/>">
            <button class="btn btn-track btn-lg">Suivre</button>
        </form>
    </div>
</header>
<main class="tracking-page">
    <c:choose>
    <c:when test="${rateLimited}"><div class="notice" role="status">Trop de recherches en peu de temps. Réessayez dans une minute.</div></c:when>
    <c:when test="${searched}">
        <c:choose>
        <c:when test="${not empty shipment}">
            <c:set var="events" value="${shipment.publicEvents}"/>
            <section class="waybill">
                <div class="waybill-head">
                    <div><small>Numéro de suivi</small><h2 class="waybill-no"><c:out value="${shipment.trackingNumber}"/></h2></div>
                    <t:status value="${shipment.status}"/>
                </div>
                <c:if test="${not empty events}">
                <div class="waybill-ends">
                    <div><small>Départ</small><strong><c:out value="${events[0].location}"/></strong></div>
                    <svg viewBox="0 0 70 14" fill="none" aria-hidden="true"><path d="M3 7h60" stroke="#9aa7ad" stroke-width="2" stroke-linecap="round" stroke-dasharray="1 6"/><path d="m58 2 6 5-6 5" stroke="#0e2b3d" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    <div class="to"><small>Dernière position</small><strong><c:out value="${events[fn:length(events) - 1].location}"/></strong></div>
                </div>
                </c:if>
                <div class="waybill-body">
                    <h2>Historique du suivi</h2>
                    <ol class="route">
                        <c:forEach var="event" items="${events}" varStatus="loop">
                            <li class="route-stop<c:if test="${loop.last}"> is-current</c:if>">
                                <h3><t:status value="${event.newStatus}" plain="true"/></h3>
                                <div class="where"><c:out value="${event.location}"/> — <t:datetime value="${event.createdAt}"/></div>
                                <p><c:out value="${event.description}"/></p>
                            </li>
                        </c:forEach>
                    </ol>
                </div>
            </section>
        </c:when>
        <c:otherwise><div class="notice" role="status">Aucun colis trouvé avec ce numéro de suivi.</div></c:otherwise>
        </c:choose>
    </c:when>
    </c:choose>
</main>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
