<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Tableau de bord" scope="request"/>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<div class="app-shell">
    <%@ include file="/WEB-INF/views/fragments/sidebar.jspf" %>
    <main class="main-content">
        <div class="topbar">
            <div><span class="topbar-meta"><c:out value="${sessionScope.currentUser.role.label}"/></span><h1>Tableau de bord</h1></div>
            <div class="topbar-user"><span><c:out value="${sessionScope.currentUser.name}"/> · <c:out value="${sessionScope.currentUser.agencyName}"/></span><span class="avatar" aria-hidden="true">T</span></div>
        </div>
        <section class="ledger" aria-label="Statistiques des colis">
            <div class="ledger-cell"><span>Total des colis</span><strong>${stats.total}</strong><small>Tous les colis visibles pour vous</small></div>
            <div class="ledger-cell"><span>En attente</span><strong>${stats.pending}</strong><small>Enregistrés, à expédier</small></div>
            <div class="ledger-cell"><span>En cours</span><strong>${stats.inProgress}</strong><small>Expédiés, en transit, en livraison</small></div>
            <div class="ledger-cell"><span>Livrés</span><strong>${stats.delivered}</strong><small>Remis au destinataire</small></div>
            <div class="ledger-cell"><span>Incidents</span><strong>${stats.incidents}</strong><small>Échecs, retours, annulations</small></div>
        </section>
        <section class="panel mt-4">
            <div class="panel-head">
                <div><h2>Derniers colis</h2><p>Les expéditions récemment enregistrées</p></div>
                <c:if test="${sessionScope.currentUser.role.canCreate()}">
                    <a class="btn btn-track btn-sm" href="${pageContext.request.contextPath}/shipments/new">+ Nouveau colis</a>
                </c:if>
            </div>
            <div class="table-responsive"><table class="table align-middle"><thead><tr><th>Suivi</th><th>Destinataire</th><th>Statut</th><th>Date</th></tr></thead><tbody>
            <c:choose><c:when test="${not empty recentShipments}">
                <c:forEach var="s" items="${recentShipments}">
                    <tr><td class="track-no"><c:out value="${s.trackingNumber}"/></td><td><c:out value="${s.recipientName}"/></td><td><t:status value="${s.status}"/></td><td class="cell-date"><t:datetime value="${s.createdAt}"/></td></tr>
                </c:forEach>
            </c:when><c:otherwise><tr><td colspan="4" class="empty-row">Aucun colis enregistré pour le moment.</td></tr></c:otherwise></c:choose>
            </tbody></table></div>
        </section>
    </main>
</div>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
