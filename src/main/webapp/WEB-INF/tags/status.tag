<%@ tag body-content="empty" pageEncoding="UTF-8" description="Affiche un statut de colis (libellé français + couleur)" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="value" required="false" rtexprvalue="true" type="java.lang.Object" %>
<%@ attribute name="plain" required="false" rtexprvalue="true" type="java.lang.Boolean" description="true = libellé seul, sans pastille" %>
<c:set var="code"><c:out value="${value}"/></c:set>
<c:set var="tone" value="neutral"/>
<c:choose>
    <c:when test="${code == 'REGISTERED'}"><c:set var="label" value="Enregistré"/></c:when>
    <c:when test="${code == 'PENDING'}"><c:set var="label" value="En attente"/><c:set var="tone" value="wait"/></c:when>
    <c:when test="${code == 'SHIPPED'}"><c:set var="label" value="Expédié"/><c:set var="tone" value="transit"/></c:when>
    <c:when test="${code == 'IN_TRANSIT'}"><c:set var="label" value="En transit"/><c:set var="tone" value="transit"/></c:when>
    <c:when test="${code == 'ARRIVED'}"><c:set var="label" value="Arrivé"/><c:set var="tone" value="transit"/></c:when>
    <c:when test="${code == 'READY_FOR_PICKUP'}"><c:set var="label" value="Prêt au retrait"/><c:set var="tone" value="wait"/></c:when>
    <c:when test="${code == 'OUT_FOR_DELIVERY'}"><c:set var="label" value="En cours de livraison"/><c:set var="tone" value="transit"/></c:when>
    <c:when test="${code == 'DELIVERED'}"><c:set var="label" value="Livré"/><c:set var="tone" value="done"/></c:when>
    <c:when test="${code == 'DELIVERY_FAILED'}"><c:set var="label" value="Échec de livraison"/><c:set var="tone" value="alert"/></c:when>
    <c:when test="${code == 'RETURNED'}"><c:set var="label" value="Retourné"/><c:set var="tone" value="off"/></c:when>
    <c:when test="${code == 'CANCELLED'}"><c:set var="label" value="Annulé"/><c:set var="tone" value="off"/></c:when>
    <c:otherwise><c:set var="label" value="${code}"/></c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${plain}"><c:out value="${label}"/></c:when>
    <c:otherwise><span class="status status--${tone}"><c:out value="${label}"/></span></c:otherwise>
</c:choose>
