// FilterUsers.js — filtres et recherche dynamique (users.html)
 
let currentStatusFilter = "all";
let currentRoleFilter   = "all";
 
function applyFilters(status, role) {
    currentStatusFilter = status;
    currentRoleFilter   = role;
 
    const search = document.getElementById("searchInput")?.value.toLowerCase() || "";
    const cards  = document.querySelectorAll(".user-card");
 
    cards.forEach(card => {
        const text       = card.innerText.toLowerCase();
        const cardStatus = card.getAttribute("data-status");
        const cardRole   = card.getAttribute("data-role");
 
        const matchSearch = text.includes(search);
        const matchStatus = status === "all" || cardStatus === status;
        const matchRole   = role   === "all" || cardRole   === role;
 
        card.style.display = matchSearch && matchStatus && matchRole ? "flex" : "none";
    });
 
    updateActiveButtons(status, role);
}
 
function dynamicSearch() {
    applyFilters(currentStatusFilter, currentRoleFilter);
}
 
// Met en surbrillance le bouton de filtre actif
function updateActiveButtons(status, role) {
    document.querySelectorAll("[data-filter-status]").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.filterStatus === status);
    });
    document.querySelectorAll("[data-filter-role]").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.filterRole === role);
    });
}

//Recherche par nom, prénom ou email  (coté client)
document.addEventListener("DOMContentLoaded", function () {
	//par défaut tous
	updateActiveButtons(currentStatusFilter, currentRoleFilter);
    const input = document.getElementById("searchInput");
    if (input) {
        input.addEventListener("keyup", dynamicSearch);
    }
});