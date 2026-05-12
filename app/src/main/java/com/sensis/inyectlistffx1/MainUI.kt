package com.sensis.inyectlistffx1

import android.animation.*
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.animation.*
import android.widget.*
import androidx.core.content.ContextCompat
import java.security.MessageDigest

class MainUI(private val activity: MainActivity) {

    val rootView: FrameLayout
    private val prefs: SharedPreferences = activity.prefs

    // Colors
    private val cGreen  = Color.parseColor("#00FF41")
    private val cGreen2 = Color.parseColor("#00C832")
    private val cRed    = Color.parseColor("#FF2244")
    private val cBlue   = Color.parseColor("#00AAFF")
    private val cPurple = Color.parseColor("#BB44FF")
    private val cBg     = Color.parseColor("#050A05")
    private val cBg2    = Color.parseColor("#0A0F0A")
    private val cDim    = Color.parseColor("#1A2A1A")

    // Views
    private lateinit var loginScreen: LinearLayout
    private lateinit var dashboard: LinearLayout
    private lateinit var pwInput: EditText
    private lateinit var loginBtn: Button
    private lateinit var shizukuBadge: TextView
    private lateinit var consoleLog: TextView
    private lateinit var scrollView: ScrollView
    private lateinit var progressBar: ProgressBar
    private lateinit var progressTxt: TextView
    private lateinit var statusTxt: TextView

    // Password hash (SHA-256 of "by SENSIS GOOD")
    private val PW_HASH = "8e3b2c1a9f4d6e5b7a2c8d1e3f5a7b9c2d4e6f8a1b3c5d7e9f2a4b6c8d0e2f4"

    init {
        rootView = FrameLayout(activity).apply {
            setBackgroundColor(cBg)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        buildLoginScreen()
        buildDashboard()
        rootView.addView(loginScreen)
        rootView.addView(dashboard)
        dashboard.visibility = View.GONE
        animateLoginIn()
    }

    // ─── LOGIN SCREEN ────────────────────────────────────────────────────
    private fun buildLoginScreen() {
        loginScreen = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(cBg)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(dp(24), dp(32), dp(24), dp(32))
        }

        // ASCII Banner
        val banner = TextView(activity).apply {
            text = """
███████╗███████╗███╗  ██╗███████╗██╗███████╗
██╔════╝██╔════╝████╗ ██║██╔════╝██║██╔════╝
███████╗█████╗  ██╔██╗██║███████╗██║███████╗
╚════██║██╔══╝  ██║╚████║╚════██║██║╚════██║
███████║███████╗██║ ╚███║███████║██║███████║
╚══════╝╚══════╝╚═╝  ╚══╝╚══════╝╚═╝╚══════╝
            """.trimIndent()
            setTextColor(cGreen)
            textSize = 6f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
        }

        val subTitle = TextView(activity).apply {
            text = "GOOD FF · TouchInject v2 · SHIZUKU EDITION"
            setTextColor(cBlue)
            textSize = 10f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(24))
        }

        val divider1 = View(activity).apply {
            setBackgroundColor(cGreen2)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1)
            ).also { it.bottomMargin = dp(24) }
        }

        val lockIcon = TextView(activity).apply {
            text = "🔐"
            textSize = 36f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(16))
        }

        val pwLabel = TextView(activity).apply {
            text = "> ACCESO RESTRINGIDO — INGRESA CLAVE:"
            setTextColor(cGreen)
            textSize = 11f
            typeface = Typeface.MONOSPACE
            setPadding(0, 0, 0, dp(8))
        }

        pwInput = EditText(activity).apply {
            hint = "••••••••••••"
            setHintTextColor(Color.parseColor("#336633"))
            setTextColor(cGreen)
            textSize = 16f
            typeface = Typeface.MONOSPACE
            inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            background = makeBorderBg(cGreen2, cDim)
            setPadding(dp(16), dp(12), dp(16), dp(12))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(16) }
        }

        // Pre-fill saved password
        val saved = prefs.getString("saved_pw_enc", "")
        if (!saved.isNullOrEmpty()) {
            pwInput.setText(deobfuscate(saved))
        }

        loginBtn = Button(activity).apply {
            text = "[ ACCEDER ]"
            setTextColor(cBg)
            textSize = 14f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setBackgroundColor(cGreen)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = dp(12) }
            setPadding(0, dp(14), 0, dp(14))
            setOnClickListener { checkLoginAndProceed() }
        }

        val savePwRow = LinearLayout(activity).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val savePwCb = CheckBox(activity).apply {
            isChecked = !saved.isNullOrEmpty()
            setButtonTintList(android.content.res.ColorStateList.valueOf(cGreen))
        }
        val savePwLbl = TextView(activity).apply {
            text = " Guardar contraseña"
            setTextColor(cGreen2)
            textSize = 12f
            typeface = Typeface.MONOSPACE
        }
        savePwRow.addView(savePwCb)
        savePwRow.addView(savePwLbl)

        val errorTxt = TextView(activity).apply {
            id = View.generateViewId()
            text = ""
            setTextColor(cRed)
            textSize = 11f
            typeface = Typeface.MONOSPACE
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
            tag = "errorTxt"
        }

        fun checkLoginAndProceed() { val pw = pwInput.text.toString(); if (checkPassword(pw)) { if (savePwCb.isChecked) prefs.edit().putString("saved_pw_enc", obfuscate(pw)).apply() else prefs.edit().remove("saved_pw_enc").apply(); animateLoginSuccess() } else { shakeView(pwInput) } }
        loginBtn.setOnClickListener {
            val pw = pwInput.text.toString()
            if (checkPassword(pw)) {
                if (savePwCb.isChecked) prefs.edit().putString("saved_pw_enc", obfuscate(pw)).apply()
                else prefs.edit().remove("saved_pw_enc").apply()
                animateLoginSuccess()
            } else {
                errorTxt.text = "✘ CLAVE INCORRECTA — ACCESO DENEGADO"
                shakeView(pwInput)
            }
        }

        loginScreen.addView(banner)
        loginScreen.addView(subTitle)
        loginScreen.addView(divider1)
        loginScreen.addView(lockIcon)
        loginScreen.addView(pwLabel)
        loginScreen.addView(pwInput)
        loginScreen.addView(loginBtn)
        loginScreen.addView(savePwRow)
        loginScreen.addView(errorTxt)
    }

    // ─── DASHBOARD ────────────────────────────────────────────────────────
    fun buildDashboard() {
        dashboard = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(cBg)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val scroll = ScrollView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        val inner = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }

        // Header
        val header = TextView(activity).apply {
            text = "▸ SENSIS GOOD · TouchInject v2"
            setTextColor(cGreen)
            textSize = 14f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            setPadding(0, 0, 0, dp(4))
        }

        val deviceTxt = TextView(activity).apply {
            text = "▸ ${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE}"
            setTextColor(cBlue)
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setPadding(0, 0, 0, dp(12))
        }

        shizukuBadge = TextView(activity).apply {
            text = "◉ SHIZUKU: VERIFICANDO..."
            setTextColor(cPurple)
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setPadding(0, 0, 0, dp(16))
        }

        val div = View(activity).apply {
            setBackgroundColor(cDim)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1)
            ).also { it.bottomMargin = dp(16) }
        }

        // Buttons
        fun makeBtn(label: String, color: Int, type: String): Button {
            return Button(activity).apply {
                text = label
                setTextColor(Color.BLACK)
                textSize = 12f
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                setBackgroundColor(color)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = dp(10) }
                setPadding(0, dp(14), 0, dp(14))
                setOnClickListener { activity.runInjection(type) }
            }
        }

        val btnAll    = makeBtn("⚡ APLICAR TODO (TOUCH + FPS + GLOBAL)", cGreen, "ALL")
        val btnFps    = makeBtn("🎮 FPS ESTABLES + OPTIMIZACIÓN MÁXIMA", cPurple, "FPS")
        val btnTouch  = makeBtn("👆 TOUCH INJECT (sensibilidad)", cBlue, "TOUCH")
        val btnGlobal = makeBtn("⚙ GLOBAL (animaciones + juego)", Color.parseColor("#FF8800"), "GLOBAL")
        val btnSecure = makeBtn("🔒 SECURE (tap + latencia)", Color.parseColor("#00CCAA"), "SECURE")
        val btnProps  = makeBtn("🔧 PROPS (GPU + RAM + hardware)", cRed, "PROPS")

        // Progress
        progressBar = ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100; progress = 0
            progressTintList = android.content.res.ColorStateList.valueOf(cGreen)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).also { it.topMargin = dp(8); it.bottomMargin = dp(4) }
        }

        progressTxt = TextView(activity).apply {
            text = ""
            setTextColor(cGreen2)
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setPadding(0, 0, 0, dp(8))
        }

        statusTxt = TextView(activity).apply {
            text = "> Listo. Selecciona una opción."
            setTextColor(cGreen)
            textSize = 11f
            typeface = Typeface.MONOSPACE
            setPadding(0, 0, 0, dp(8))
        }

        // Console
        val consoleLabel = TextView(activity).apply {
            text = "─── CONSOLA ────────────────────────────────"
            setTextColor(cDim.let { Color.parseColor("#335533") })
            textSize = 10f
            typeface = Typeface.MONOSPACE
            setPadding(0, dp(8), 0, dp(4))
        }

        consoleLog = TextView(activity).apply {
            text = "> Sistema listo...\n"
            setTextColor(cGreen2)
            textSize = 9f
            typeface = Typeface.MONOSPACE
            setBackgroundColor(cBg2)
            setPadding(dp(10), dp(10), dp(10), dp(10))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(280)
            )
        }

        scrollView = ScrollView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(280)
            ).also { it.bottomMargin = dp(16) }
            addView(consoleLog)
        }

        inner.addView(header)
        inner.addView(deviceTxt)
        inner.addView(shizukuBadge)
        inner.addView(div)
        inner.addView(btnAll)
        inner.addView(btnFps)
        inner.addView(btnTouch)
        inner.addView(btnGlobal)
        inner.addView(btnSecure)
        inner.addView(btnProps)
        inner.addView(progressBar)
        inner.addView(progressTxt)
        inner.addView(statusTxt)
        inner.addView(consoleLabel)
        inner.addView(scrollView)

        scroll.addView(inner)
        dashboard.addView(scroll)
    }

    // ─── CONSOLE LOGGING ─────────────────────────────────────────────────
    private var logBuffer = StringBuilder("> Sistema listo...\n")
    private var totalCmds = 0

    fun logLine(cmd: String, current: Int, total: Int) {
        totalCmds = total
        val pct = (current * 100) / total
        progressBar.progress = pct
        progressTxt.text = "[$current/$total] ${pct}%"

        val color = when {
            cmd.startsWith("settings put system") -> cGreen
            cmd.startsWith("settings put global") -> cBlue
            cmd.startsWith("settings put secure") -> Color.parseColor("#00CCAA")
            cmd.startsWith("setprop") -> cPurple
            else -> cGreen2
        }
        val prefix = when (color) {
            cGreen  -> "SYS"
            cBlue   -> "GLB"
            cPurple -> "PRP"
            else    -> "CMD"
        }

        logBuffer.append("[$prefix] $cmd\n")
        if (logBuffer.length > 8000) logBuffer.delete(0, 2000)

        val span = SpannableString(logBuffer.toString())
        val lines = logBuffer.lines()
        var pos = 0
        lines.forEach { line ->
            val c = when {
                line.startsWith("[SYS]") -> cGreen
                line.startsWith("[GLB]") -> cBlue
                line.startsWith("[PRP]") -> cPurple
                line.startsWith("[ERR]") -> cRed
                line.startsWith("[OK]")  -> cGreen
                else -> cGreen2
            }
            span.setSpan(ForegroundColorSpan(c), pos, minOf(pos + line.length, span.length),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            pos += line.length + 1
        }
        consoleLog.text = span
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    fun logError(cmd: String) {
        logBuffer.append("[ERR] $cmd\n")
        consoleLog.text = logBuffer.toString()
    }

    fun onDone(type: String, ok: Int, fail: Int) {
        progressBar.progress = 100
        progressTxt.text = "✔ COMPLETADO"
        statusTxt.text = "> $type → ✔ $ok OK  ✘ $fail errores"
        statusTxt.setTextColor(if (fail == 0) cGreen else cRed)
        logBuffer.append("\n[OK] ═══ $type COMPLETADO · $ok/ok · $fail/err ═══\n\n")
        consoleLog.text = logBuffer.toString()
        scrollView.post { scrollView.fullScroll(View.FOCUS_DOWN) }
    }

    fun setShizukuStatus(active: Boolean) {
        shizukuBadge.text = if (active)
            "◉ SHIZUKU: ACTIVO ✔ — Comandos disponibles"
        else
            "◉ SHIZUKU: INACTIVO ✘ — Inicia Shizuku primero"
        shizukuBadge.setTextColor(if (active) cGreen else cRed)
    }

    fun showDashboard() {
        loginScreen.animate().alpha(0f).setDuration(400).withEndAction {
            loginScreen.visibility = View.GONE
            dashboard.visibility = View.VISIBLE
            dashboard.alpha = 0f
            dashboard.animate().alpha(1f).setDuration(500).start()
        }.start()
    }

    // ─── ANIMATIONS ──────────────────────────────────────────────────────
    private fun animateLoginIn() {
        loginScreen.alpha = 0f
        loginScreen.translationY = 60f
        loginScreen.animate().alpha(1f).translationY(0f)
            .setDuration(700).setInterpolator(DecelerateInterpolator()).start()
    }

    private fun animateLoginSuccess() {
        loginBtn.text = "[ ACCESO CONCEDIDO ✔ ]"
        loginBtn.setBackgroundColor(cGreen)
        val flash = ObjectAnimator.ofFloat(loginScreen, "alpha", 1f, 0.3f, 1f, 0.3f, 1f)
        flash.duration = 600
        flash.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(a: Animator) { showDashboard() }
        })
        flash.start()
    }

    private fun shakeView(v: View) {
        val shake = TranslateAnimation(-20f, 20f, 0f, 0f).apply {
            duration = 80; repeatCount = 4
            repeatMode = Animation.REVERSE
        }
        v.startAnimation(shake)
    }

    // ─── PASSWORD ────────────────────────────────────────────────────────
    private fun checkPassword(pw: String): Boolean {
        val hash = sha256("GTinj_s3cur3${pw}GTinj_s3cur3")
        return hash == "366d0dc2ea33810dc8089ee959ad1b7c50e06d468d3a1880b4f995b43a424261"
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun obfuscate(s: String): String =
        s.toByteArray().joinToString(",") { (it.toInt() xor 0x5A).toString() }

    private fun deobfuscate(s: String): String = try {
        String(s.split(",").map { (it.toInt() xor 0x5A).toByte() }.toByteArray())
    } catch (e: Exception) { "" }

    // ─── HELPERS ─────────────────────────────────────────────────────────
    private fun dp(v: Int) = (v * activity.resources.displayMetrics.density).toInt()

    private fun makeBorderBg(border: Int, fill: Int): android.graphics.drawable.GradientDrawable {
        return android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            setColor(fill)
            setStroke(dp(1), border)
            cornerRadius = dp(4).toFloat()
        }
    }
}
