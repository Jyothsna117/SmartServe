/**
 * SmartServe Client-side Enhancements
 */
document.addEventListener("DOMContentLoaded", function () {
    // Automatically uppercase tracking ID input fields for convenience
    const trackingInputs = document.querySelectorAll('input[name="trackingId"], #trackingId');
    trackingInputs.forEach(input => {
        input.addEventListener("input", function () {
            this.value = this.value.toUpperCase();
        });
    });

    // Copy to clipboard helper
    const copyButtons = document.querySelectorAll(".btn-copy-tracking");
    copyButtons.forEach(button => {
        button.addEventListener("click", function () {
            const targetId = this.getAttribute("data-target");
            const textElement = document.getElementById(targetId);
            if (textElement) {
                const textToCopy = textElement.innerText || textElement.textContent;
                navigator.clipboard.writeText(textToCopy.trim()).then(() => {
                    const originalText = this.innerHTML;
                    this.innerHTML = '<i class="bi bi-check2"></i> Copied!';
                    this.classList.remove("btn-outline-primary");
                    this.classList.add("btn-success");
                    setTimeout(() => {
                        this.innerHTML = originalText;
                        this.classList.remove("btn-success");
                        this.classList.add("btn-outline-primary");
                    }, 2000);
                }).catch(err => {
                    console.error("Failed to copy tracking ID: ", err);
                });
            }
        });
    });

    // Auto-dismiss alerts after 5 seconds if marked with .auto-dismiss
    const autoDismissAlerts = document.querySelectorAll(".alert.auto-dismiss");
    autoDismissAlerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
});
