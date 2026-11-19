<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<div class="app-shell">
    <%@ include file="/WEB-INF/views/fragments/sidebar.jspf" %>
    <main class="main-content">
        <div class="topbar">
            <div><span class="topbar-meta">Gestion</span><h1>Tous les colis</h1></div>
            <a class="btn btn-track" href="${pageContext.request.contextPath}/shipments/new">+ Ajouter un colis</a>
        </div>
        <section class="panel mt-4">
            <div class="table-responsive"><table class="table align-middle"><thead><tr><th>Numéro de suivi</th><th>Expéditeur</th><th>Destinataire</th><th>Statut</th><th>Créé le</th></tr></thead><tbody>
            <c:forEach var="s" items="${shipments}">
                <tr><td class="track-no">${s.trackingNumber}</td><td>${s.senderName}</td><td>${s.recipientName}</td><td><t:status value="${s.status}"/></td><td class="cell-date"><t:datetime value="${s.createdAt}"/></td></tr>
            </c:forEach>
            <c:if test="${empty shipments}"><tr><td colspan="5" class="empty-row">Aucun colis. Ajoutez votre premier colis.</td></tr></c:if>
            </tbody></table></div>
        </section>
    </main>
</div>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
