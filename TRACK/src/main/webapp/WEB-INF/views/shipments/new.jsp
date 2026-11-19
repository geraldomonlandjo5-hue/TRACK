<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nouveau colis" scope="request"/>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>
<div class="app-shell">
    <%@ include file="/WEB-INF/views/fragments/sidebar.jspf" %>
    <main class="main-content">
        <div class="topbar"><div><span class="topbar-meta">Gestion des colis</span><h1>Enregistrer un colis</h1></div></div>
        <section class="panel mt-4">
            <c:if test="${not empty error}"><div class="alert alert-danger" role="alert"><c:out value="${error}"/></div></c:if>
            <form method="post" action="${pageContext.request.contextPath}/shipments/new">
                <input type="hidden" name="csrfToken" value="<c:out value='${sessionScope.csrfToken}'/>">
                <div class="form-section">
                    <div><h2>Expéditeur</h2><p>La personne qui dépose le colis.</p></div>
                    <div class="row g-3">
                        <div class="col-md-6"><label class="form-label" for="senderName">Nom complet *</label><input class="form-control" id="senderName" name="senderName" required maxlength="120" value="<c:out value='${param.senderName}'/>"></div>
                        <div class="col-md-6"><label class="form-label" for="senderPhone">Téléphone</label><input class="form-control" id="senderPhone" name="senderPhone" maxlength="30" value="<c:out value='${param.senderPhone}'/>"></div>
                        <div class="col-12"><label class="form-label" for="senderAddress">Adresse de départ</label><input class="form-control" id="senderAddress" name="senderAddress" maxlength="255" value="<c:out value='${param.senderAddress}'/>"></div>
                    </div>
                </div>
                <div class="form-section">
                    <div><h2>Destinataire</h2><p>La personne qui recevra le colis.</p></div>
                    <div class="row g-3">
                        <div class="col-md-6"><label class="form-label" for="recipientName">Nom complet *</label><input class="form-control" id="recipientName" name="recipientName" required maxlength="120" value="<c:out value='${param.recipientName}'/>"></div>
                        <div class="col-md-6"><label class="form-label" for="recipientPhone">Téléphone</label><input class="form-control" id="recipientPhone" name="recipientPhone" maxlength="30" value="<c:out value='${param.recipientPhone}'/>"></div>
                        <div class="col-12"><label class="form-label" for="recipientAddress">Adresse de destination</label><input class="form-control" id="recipientAddress" name="recipientAddress" maxlength="255" value="<c:out value='${param.recipientAddress}'/>"></div>
                    </div>
                </div>
                <div class="form-section">
                    <div><h2>Colis</h2><p>Poids et contenu.</p></div>
                    <div class="row g-3">
                        <div class="col-md-6"><label class="form-label" for="weight">Poids (kg)</label><input class="form-control" id="weight" type="number" name="weight" min="0.01" step="0.01" value="<c:out value='${param.weight}'/>"></div>
                        <div class="col-12"><label class="form-label" for="description">Description du colis</label><textarea class="form-control" id="description" name="description" rows="3" maxlength="500"><c:out value="${param.description}"/></textarea></div>
                    </div>
                </div>
                <div class="form-actions"><a class="btn btn-quiet" href="${pageContext.request.contextPath}/shipments">Annuler</a><button class="btn btn-track">Enregistrer le colis</button></div>
            </form>
        </section>
    </main>
</div>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
