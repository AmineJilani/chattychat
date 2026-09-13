// FilterChat.js — filtres par statut et recherche par titre (chats.html)
 
let currentChatFilter = "all";
 
function filterChats(status) {
    currentChatFilter = status;
 
    const search = document.getElementById("searchChatInput")?.value.toLowerCase() || "";
    const cards  = document.querySelectorAll(".chat-card");
 
    cards.forEach(card => {
        const cardStatus = card.getAttribute("data-status");
        const title = card.querySelector("h2")?.innerText.toLowerCase() || "";
 
        const matchStatus = status === "all" || cardStatus === status;
        const matchSearch = title.includes(search);
 
        card.style.display = matchStatus && matchSearch ? "flex" : "none";
    });
 
    updateActiveChatButtons(status);
}
 
function dynamicChatSearch() {
    filterChats(currentChatFilter);
}
 
// Met en surbrillance le bouton de filtre actif
function updateActiveChatButtons(status) {
    document.querySelectorAll("[data-filter-chat]").forEach(btn => {
        btn.classList.toggle("active", btn.dataset.filterChat === status);
    });
}
 
document.addEventListener("DOMContentLoaded", function () {
 
    // Active "Tous" par défaut au chargement de la page
    updateActiveChatButtons(currentChatFilter);
 
    const input = document.getElementById("searchChatInput");
    if (input) {
        input.addEventListener("keyup", dynamicChatSearch);
    }
});
 