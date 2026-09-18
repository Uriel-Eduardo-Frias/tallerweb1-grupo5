const navToggle = document.getElementById("navToggle");
const navLinks = document.getElementById("navLinks");

navToggle.addEventListener("click", () => navLinks.classList.toggle("is-open"));

// Los botones de rango están listos para volver a pedir los datos al backend según el período elegido.
document.querySelectorAll("#rankTabs button").forEach((btn) => {
  btn.addEventListener("click", () => {
    document.querySelectorAll("#rankTabs button").forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
    console.log("Rango de ranking seleccionado:", btn.dataset.range);
    // TODO: reemplazar por el fetch real que trae el ranking de ese período
  });
});