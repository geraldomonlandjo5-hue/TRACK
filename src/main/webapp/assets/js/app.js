document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("form").forEach(form => {
        form.addEventListener("submit", () => {
            const submit = form.querySelector('button[type="submit"], button:not([type])');
            if (submit) {
                submit.dataset.originalText = submit.textContent;
                submit.disabled = true;
                submit.textContent = "Traitement...";
            }
        });
    });
});
