#!/data/data/com.termux/files/usr/bin/bash
# ================================================================
#  SENSIS GOOD — Script para subir proyecto a GitHub desde Termux
#  Repo: inyectListFFx1
# ================================================================

G=$'\e[0;32m'; R=$'\e[0;31m'; Y=$'\e[1;33m'; B=$'\e[1;34m'; N=$'\e[0m'

echo ""
echo "${Y}══════════════════════════════════════════${N}"
echo "${G}  SENSIS GOOD · Subir a GitHub desde Termux${N}"
echo "${Y}══════════════════════════════════════════${N}"
echo ""

# 1. Instalar dependencias si faltan
echo "${B}[1/6] Instalando git y gh...${N}"
pkg install -y git gh 2>/dev/null
echo "${G}  ✔ Listo${N}"

# 2. Configurar git
echo "${B}[2/6] Configura tu nombre y email de GitHub:${N}"
printf "  Nombre (ej: SENSIS GOOD): "
read GIT_NAME
printf "  Email GitHub: "
read GIT_EMAIL
git config --global user.name "$GIT_NAME"
git config --global user.email "$GIT_EMAIL"
echo "${G}  ✔ Git configurado${N}"

# 3. Login a GitHub CLI
echo ""
echo "${B}[3/6] Login a GitHub (abrirá el navegador o pedirá token):${N}"
gh auth login
echo "${G}  ✔ Login OK${N}"

# 4. Crear repo en GitHub
echo ""
echo "${B}[4/6] Creando repositorio inyectListFFx1 en GitHub...${N}"
gh repo create inyectListFFx1 --public --description "SENSIS GOOD FF · TouchInject v2 · Shizuku APK" 2>/dev/null \
  && echo "${G}  ✔ Repo creado${N}" \
  || echo "${Y}  ℹ Repo ya existe, continuando...${N}"

# 5. Inicializar git y subir
echo ""
echo "${B}[5/6] Subiendo archivos...${N}"
cd ~/inyectListFFx1 || { echo "${R}  ✘ Carpeta no encontrada. Extrae el ZIP primero.${N}"; exit 1; }

git init 2>/dev/null
git remote remove origin 2>/dev/null
git remote add origin "https://github.com/$GIT_NAME/inyectListFFx1.git" 2>/dev/null \
  || git remote add origin "$(gh repo view inyectListFFx1 --json url -q .url).git"

git add .
git commit -m "SENSIS GOOD FF v2 - TouchInject + FPS + Shizuku"
git branch -M main
git push -u origin main --force
echo "${G}  ✔ Subido a GitHub${N}"

# 6. Activar GitHub Actions
echo ""
echo "${B}[6/6] Activando compilación automática...${N}"
gh workflow run build.yml 2>/dev/null || echo "${Y}  ℹ Se activará al hacer push${N}"

echo ""
echo "${Y}══════════════════════════════════════════${N}"
echo "${G}  ✔ LISTO. GitHub Actions compilará la APK.${N}"
echo ""
echo "${B}  Para descargar la APK cuando termine:${N}"
echo "${G}  gh run download --repo $GIT_NAME/inyectListFFx1${N}"
echo ""
echo "${B}  O ábrela en el navegador:${N}"
echo "${G}  https://github.com/$GIT_NAME/inyectListFFx1/actions${N}"
echo "${Y}══════════════════════════════════════════${N}"
echo ""
