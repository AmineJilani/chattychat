// passsword.js : 
// Génération d'un mot de passe sécurisé
// Garantit : 1 majuscule, 1 minuscule, 1 chiffre, 1 spécial

function generatePassword() {
    const length = 12;
    const upper  = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const lower  = "abcdefghijklmnopqrstuvwxyz";
    const digits = "0123456789";
    const special = "!@#$%^&*()_+-=";
    const all = upper + lower + digits + special;
 
    // On garantit au moins un caractère de chaque type
    let password = [
        upper  [Math.floor(Math.random() * upper.length)],
        lower  [Math.floor(Math.random() * lower.length)],
        digits [Math.floor(Math.random() * digits.length)],
        special[Math.floor(Math.random() * special.length)]
    ];
 
    // On complète jusqu'à la longueur voulue
    for (let i = password.length; i < length; i++) {
        password.push(all[Math.floor(Math.random() * all.length)]);
    }
 
    // On mélange pour ne pas avoir majuscule toujours en premier
    password = password.sort(() => Math.random() - 0.5).join('');
 
    const field = document.getElementById("password");
    const confirm = document.getElementById("confirmPassword");
 
    if (field) field.value = password;
    if (confirm) confirm.value = password;
 
    // Affiche le mot de passe généré en clair temporairement
    showPassword(true);
    updateStrengthIndicator(password);
    validatePasswordMatch();
}
 
// Afficher / masquer le mot de passe
function showPassword(force) {
    const fields = ["password", "confirmPassword"];
 
    fields.forEach(id => {
        const field = document.getElementById(id);
        if (!field) return;
        field.type = force === true ? "text" : (field.type === "password" ? "text" : "password");
    });
}
 
// Indicateur de force du mot de passe
function updateStrengthIndicator(value) {
    const indicator = document.getElementById("strengthIndicator");
    if (!indicator) return;
 
    const checks = [
        value.length >= 12,
        /[A-Z]/.test(value),
        /[a-z]/.test(value),
        /[0-9]/.test(value),
        /[!@#$%^&*()_+\-=]/.test(value)
    ];
 
    const score = checks.filter(Boolean).length;
 
    const levels = [
        { label: "",          color: "transparent" },
        { label: "Très faible", color: "#e74c3c" },
        { label: "Faible",      color: "#e67e22" },
        { label: "Moyen",       color: "#f1c40f" },
        { label: "Fort",        color: "#2ecc71" },
        { label: "Très fort",   color: "#27ae60" }
    ];
 
    const level = levels[score];
    indicator.textContent = value.length > 0 ? "Force : " + level.label : "";
    indicator.style.color = level.color;
}
 
// Validation de la confirmation du mot de passe
function validatePasswordMatch() {
    const password = document.getElementById("password");
    const confirm  = document.getElementById("confirmPassword");
    const error    = document.getElementById("passwordMatchError");
 
    if (!password || !confirm || !error) return true;
 
    if (confirm.value.length === 0) {
        error.textContent = "";
        return true;
    }
 
    if (password.value !== confirm.value) {
        error.textContent = "Les mots de passe ne correspondent pas";
        error.style.color = "red";
        return false;
    }
 
    error.textContent = "Mots de passe identiques";
    error.style.color = "green";
    return true;
}
 
// Validation complète du formulaire avant soumission
function validateUserForm(event) {
    const password = document.getElementById("password");
    const confirm  = document.getElementById("confirmPassword");
 
    if (!password) return true;
 
    // Vérification longueur
    if (password.value.length < 12) {
        event.preventDefault();
        alert("Le mot de passe doit contenir au moins 12 caractères.");
        return false;
    }
 
    // Vérification complexité
    const rules = [
        { test: /[A-Z]/, msg: "une majuscule" },
        { test: /[a-z]/, msg: "une minuscule" },
        { test: /[0-9]/, msg: "un chiffre" },
        { test: /[!@#$%^&*()_+\-=]/, msg: "un caractère spécial" }
    ];
 
    for (const rule of rules) {
        if (!rule.test.test(password.value)) {
            event.preventDefault();
            alert("Le mot de passe doit contenir au moins " + rule.msg + ".");
            return false;
        }
    }
 
    // Vérification correspondance
    if (confirm && password.value !== confirm.value) {
        event.preventDefault();
        alert("Les mots de passe ne correspondent pas.");
        return false;
    }
 
    return true;
}
 
// Initialisation au chargement de la page
document.addEventListener("DOMContentLoaded", function () {
 
    // Indicateur de force en temps réel
    const passwordField = document.getElementById("password");
    if (passwordField) {
        passwordField.addEventListener("input", function () {
            updateStrengthIndicator(this.value);
            validatePasswordMatch();
        });
    }
 
    // Validation confirmation en temps réel
    const confirmField = document.getElementById("confirmPassword");
    if (confirmField) {
        confirmField.addEventListener("input", validatePasswordMatch);
    }
 
    // Validation formulaire à la soumission
    const form = document.querySelector("form");
    if (form && document.getElementById("password")) {
        form.addEventListener("submit", validateUserForm);
    }
});