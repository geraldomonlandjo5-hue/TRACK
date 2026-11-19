<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Gestion des colis" scope="request"/>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<div class="app-shell">
    <%@ include file="/WEB-INF/views/fragments/sidebar.jspf" %>
    <main class="main-content">
        <div class="topbar">
            <div><span class="topbar-meta">Gestion</span><h1>Tous les colis</h1></div>
            <c:if test="${sessionScope.currentUser.role.canCreate()}">
                <a class="btn btn-track" href="${pageContext.request.contextPath}/shipments/new">+ Ajouter un colis</a>
            </c:if>
        </div>
        <section class="panel mt-4">
            <c:if test="${not empty sessionScope.flash}">
                <div class="alert alert-info" role="status"><c:out value="${sessionScope.flash}"/></div>
                <c:remove var="flash" scope="session"/>
            </c:if>

            <form class="filters" method="get" action="${pageContext.request.contextPath}/shipments">
                <input class="form-control" type="search" name="q" maxlength="80" aria-label="Rechercher" placeholder="Numéro de suivi, expéditeur ou destinataire" value="<c:out value='${q}'/>">
                <select class="form-select" name="status" aria-label="Filtrer par statut">
                    <option value="">Tous les statuts</option>
                    <c:forEach var="st" items="${statuses}"><option value="${st}" <c:if test="${st.name() == statusFilter}">selected</c:if>><c:out value="${st.label}"/></option></c:forEach>
                </select>
                <button class="btn btn-track">Filtrer</button>
                <c:if test="${not empty q or not empty statusFilter}"><a class="btn btn-quiet" href="${pageContext.request.contextPath}/shipments">Réinitialiser</a></c:if>
            </form>
            <p class="result-count"><c:out value="${result.total}"/> colis<c:if test="${result.totalPages > 1}"> · page ${result.page + 1} sur ${result.totalPages}</c:if></p>

            <div class="table-responsive"><table class="table align-middle"><thead><tr><th>Numéro de suivi</th><th>Expéditeur</th><th>Destinataire</th><th>Statut</th><th>Créé le</th><th>Action</th></tr></thead><tbody>
            <c:forEach var="s" items="${result.items}">
                <c:set var="next" value="${allowed[s.trackingNumber]}"/>
                <tr>
                    <td class="track-no"><c:out value="${s.trackingNumber}"/></td>
                    <td><c:out value="${s.senderName}"/></td>
                    <td><c:out value="${s.recipientName}"/></td>
                    <td><t:status value="${s.status}"/></td>
                    <td class="cell-date"><t:datetime value="${s.createdAt}"/></td>
                    <td>
                        <c:choose>
                        <c:when test="${not empty next}">
                        <details><summary>Mettre à jour le suivi</summary>
                            <form method="post" action="${pageContext.request.contextPath}/shipments" class="mt-2">
                                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
                                <input type="hidden" name="action" value="status">
                                <input type="hidden" name="trackingNumber" value="<c:out value='${s.trackingNumber}'/>">
                                <input type="hidden" name="rq" value="<c:out value='${q}'/>">
                                <input type="hidden" name="rs" value="<c:out value='${statusFilter}'/>">
                                <input type="hidden" name="rp" value="${result.page}">
                                <label class="form-label">Nouveau statut</label>
                                <select class="form-select mb-2" name="status" required>
                                    <c:forEach var="st" items="${next}"><option value="${st}"><c:out value="${st.label}"/><c:if test="${st == s.status}"> (nouveau point de passage)</c:if></option></c:forEach>
                                </select>
                                <input class="form-control mb-2" name="location" placeholder="Localité / agence" required maxlength="180">
                                <input class="form-control mb-2" name="description" placeholder="Commentaire" maxlength="500">
                                <label class="check"><input type="checkbox" name="publicEvent" checked> Visible par le client</label>
                                <button class="btn btn-track btn-sm mt-2">Enregistrer</button>
                            </form>
                        </details>
                        </c:when>
                        <c:otherwise><span class="text-secondary">—</span></c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty result.items}"><tr><td colspan="6" class="empty-row">Aucun colis ne correspond.</td></tr></c:if>
            </tbody></table></div>

            <c:if test="${result.totalPages > 1}">
                <nav class="pager" aria-label="Pagination">
                    <c:if test="${result.hasPrevious}">
                        <c:url var="prevUrl" value="/shipments"><c:param name="q" value="${q}"/><c:param name="status" value="${statusFilter}"/><c:param name="page" value="${result.page - 1}"/></c:url>
                        <a class="btn btn-quiet btn-sm" href="<c:out value='${prevUrl}'/>">← Précédent</a>
                    </c:if>
                    <span>Page ${result.page + 1} / ${result.totalPages}</span>
                    <c:if test="${result.hasNext}">
                        <c:url var="nextUrl" value="/shipments"><c:param name="q" value="${q}"/><c:param name="status" value="${statusFilter}"/><c:param name="page" value="${result.page + 1}"/></c:url>
                        <a class="btn btn-quiet btn-sm" href="<c:out value='${nextUrl}'/>">Suivant →</a>
                    </c:if>
                </nav>
            </c:if>
        </section>
    </main>
</div>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
