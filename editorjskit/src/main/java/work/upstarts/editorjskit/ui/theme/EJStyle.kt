package work.upstarts.editorjskit.ui.theme

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.*
import androidx.annotation.IntRange
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import kotlinx.android.synthetic.main.item_header.view.*
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_paragraph.view.*
import work.upstarts.editorjskit.R
import work.upstarts.editorjskit.environment.dp
import work.upstarts.editorjskit.models.HeadingLevel
import work.upstarts.editorjskit.models.Margins
import work.upstarts.editorjskit.models.data.EJImageData
import work.upstarts.editorjskit.ui.views.HeaderTextView
import java.util.*
import kotlin.collections.HashMap

/**
 * Class to hold *theming* information for rending.
 */
open class EJStyle protected constructor(builder: Builder) {

    val linkColor: Int? = builder.linkColor

    // used in quote, lists
    val blockPadding: Int = builder.blockMargin
    val margins: Margins = builder.margins

    // by default uses text color (applied for un-ordered lists & ordered (bullets & numbers)
    protected val listItemColor: Int = builder.listItemColor
    protected val listBulletColor: Int = builder.listBulletColor

    protected val bulletDrawable: Int = builder.bulletDrawableRes

    // by default - main text color
    protected val paragraphTextColor: Int = builder.paragraphTextColor


    protected val headingTextColor: Int = builder.headingTextColor

    protected val paragraphBackgroundColor: Int = builder.paragraphBackgroundColor

    // by default Typeface.MONOSPACE
    protected val paragraphTypeface: Typeface? = builder.paragraphTypeface

    // by default a bit (how much?!) smaller than normal text
    // applied ONLY if default typeface was used, otherwise, not applied
    protected val paragraphTextSize: Int = builder.paragraphTextSize

    // by default, whatever typeface is set on the TextView
    protected val headingTypeface: Typeface? = builder.headingTypeface

    protected val headingTypefaceMap: HashMap<Int, Typeface> = builder.headingTypefaceMap
    protected val headingFontStyleMap: HashMap<Int, Int> = builder.headingFontStyleMap
    protected val headingColorsMap: HashMap<Int, Int> = builder.headingColorsMap

    // by default, use standard values (see HEADING_SIZES for values).
    // this library supports 6 heading sizes, so make sure the array you pass here has 6 elements.
    protected val headingTextSizeMultipliers: FloatArray? = builder.headingTextSizes
    protected val headingTextMarginsMultipliers: IntArray? = builder.headingTextMargins

    protected val delimiterColor: Int = builder.delimiterColor

    protected val delimiterHeight: Int = builder.delimiterBreakHeight

    protected val tableColumnDrawableRes: Int = builder.tableColumnDrawableRes
    protected val tableColumnTextColor: Int = builder.tableColumnTextColor

    protected val imageBackground: Int = builder.imageBackground
    protected val imageBorder: Int = builder.imageBorder

    fun applyTableColumnBackground(view: TextView) {
        if (tableColumnDrawableRes != 0) {
            view.background = view.context.getDrawable(tableColumnDrawableRes)
        }
        if (tableColumnTextColor != 0) {
            view.setTextColor(tableColumnTextColor)
        }
    }

    fun applyListItemStyle(bulletView: View, textView: TextView) {
        if (listItemColor != 0) {
            textView.setTextColor(listItemColor)
            if (listBulletColor != 0) {
                bulletView.background.setTint(listBulletColor)
            }
        }

        if (bulletDrawable != 0) {
            bulletView.background = bulletView.context.getDrawable(bulletDrawable)
        }
    }

    fun applyParagraphStyle(baseView: View, margin: Int) {
        applyParagraphTextStyle(baseView)
        applyParagraphMargin(baseView, margin)
    }

    fun applyParagraphTextStyle(baseView: View) {
        if (paragraphTextColor != 0) {
            baseView.paragraphTv.setTextColor(paragraphTextColor)
        }

        if (paragraphTypeface != null) {
            baseView.paragraphTv.typeface = paragraphTypeface
        }

        if (paragraphTextSize > 0) {
            baseView.paragraphTv.textSize = paragraphTextSize.toFloat()
        }

        applyBackgroundColor(baseView.paragraphTv)


        applyBlockPadding(baseView.paragraphTv)
    }

    private fun applyParagraphMargin(view: View, defaulMargin: Int) {
        val margins = margins.paragraphMargin
        if (margins != null) {
            applyViewMargins(margins, view)
        } else {
            view.updatePadding(
                view.paddingLeft,
                defaulMargin.dp,
                view.paddingRight,
                view.paddingBottom
            )
        }
    }

    private fun applyBlockPadding(textView: TextView) {
        textView.setPadding(blockPadding)
    }

    private fun applyBackgroundColor(textView: TextView) {
        if (paragraphBackgroundColor != 0) {
            textView.background = ColorDrawable(paragraphBackgroundColor)
        }
    }


    fun applyHeadingStyle(view: View, paint: Paint, @IntRange(from = 1, to = 6) level: Int) {
        applyHeadingTextStyle(paint, level)
        applyHeadingTextSize(view.headerTv, level)
        applyHeadingMargin(view, level)
        applyFontStyle(view.headerTv, level)
        applyHeadingTextColor(view.headerTv, level)
    }

    private fun applyHeadingTextStyle(paint: Paint, headerLevel: Int) {
        if (headingTypefaceMap[headerLevel] != null) {
            paint.typeface = headingTypefaceMap[headerLevel]
        } else {
            if (headingTypeface == null) {
                paint.isFakeBoldText = true
            } else {
                paint.typeface = headingTypeface
            }
        }
    }

    fun applyHeadingTextColor(headerTv: HeaderTextView, @IntRange(from = 1, to = 6) level: Int) {
        if (headingTextColor != 0) {
            headerTv.setTextColor(headingTextColor)
        }
        headingColorsMap[level]?.let {
             headerTv.setTextColor(it) }
        }

    fun applyHeadingTextSize(headerTv: HeaderTextView, @IntRange(from = 1, to = 6) level: Int) {
        val textSizes = headingTextSizeMultipliers ?: HEADING_SIZES

        if (textSizes.size >= level) {
            headerTv.setHeaderLevel(textSizes[level - 1])
        } else {
            throw IllegalStateException(
                String.format(
                    Locale.US,
                    "Supplied heading level: %d is invalid, where configured heading sizes are: `%s`",
                    level, textSizes.contentToString()
                )
            )
        }
    }

    fun applyHeadingMargin(view: View, @IntRange(from = 1, to = 6) level: Int) {
        if (margins.headerMargin?.get(level) == null) {
            applyDefaultHeadingMargin(view, level)
        } else {
            margins.headerMargin[level]?.let { applyViewMargins(it, view) }
        }
    }

    fun applyDefaultHeadingMargin(view: View, @IntRange(from = 1, to = 6) level: Int) {
        val marginSizes = headingTextMarginsMultipliers ?: HEADING_TOP_MARGINS_DEFAULT
        if (marginSizes.size >= level) {
            view.updatePadding(
                view.paddingLeft,
                marginSizes[level - 1].dp,
                view.paddingRight,
                view.paddingBottom
            )
        } else {
            throw IllegalStateException(
                String.format(
                    Locale.US,
                    "Supplied heading level: %d is invalid, where configured heading sizes are: `%s`",
                    level, marginSizes.contentToString()
                )
            )
        }
    }

    fun applyDefaultImageMargin(view: View, data: EJImageData) {
        if (data.withBackground)
            view.updatePadding(view.paddingLeft, 32.dp, view.paddingRight, view.paddingBottom)
    }

    fun applyDividerStyle(dividerView: TextView, container: View) {
        applyDelimiterColor(dividerView)
        applyDelimiterHeight(dividerView)
        applyDelimiterMargins(container)
    }

    private fun applyDelimiterColor(dividerView: TextView) {
        if (delimiterColor != 0) {
            dividerView.background = ColorDrawable(delimiterColor)
        }
    }

    fun applyDelimiterHeight(dividerView: TextView) {
        dividerView.updateLayoutParams<FrameLayout.LayoutParams> {
            height = delimiterHeight
        }
    }

    fun applyDelimiterMargins(dividerContainer: View) {
        margins.deviderMargin?.let { applyViewMargins(it, dividerContainer) }
    }

    fun applyImageStyle(view: View, data: EJImageData) {
        applyImageRes(view.imageView, data)
        applyImageMargin(view, data)
    }

    fun applyImageRes(imageView: ImageView, data: EJImageData) {
        if (imageBackground != 0 && data.withBackground) {
            imageView.background = imageView.context.getDrawable(imageBackground)
        }

        if (imageBorder != 0 && data.withBorder) {
            imageView.background = imageView.context.getDrawable(imageBorder)
        }
    }

    fun applyViewMargins(it: Margins.MarginData, view: View) {
        view.updatePadding(it.marginLeft.dp, it.marginTop.dp, it.marginRight.dp, it.marginBottom.dp)
    }

    fun applyImageMargin(view: View, data: EJImageData) {
        val margins = margins.imageMargin
        if (margins != null) {
            applyViewMargins(margins, view)
        } else {
            applyDefaultImageMargin(view, data)
        }
    }

    fun applyListMargin(view: View) {
        val margins = margins.listMargin
        if (margins != null) {
            applyViewMargins(margins, view)
        }
    }

    fun applyFontStyle(headerTv: HeaderTextView, level: Int) {
        headingFontStyleMap[level]?.let { headerTv.setTypeface(headerTv.typeface, it) }
    }

    class Builder {
        var margins = Margins()
        var linkColor: Int? = null
        var headingTextColor: Int = 0
        var blockMargin: Int = 0
        var listItemColor: Int = 0
        var listBulletColor: Int = 0
        var bulletDrawableRes: Int = 0
        var paragraphTextColor: Int = 0
        var paragraphBackgroundColor: Int = 0
        var paragraphTypeface: Typeface? = null
        val headingTypefaceMap: HashMap<Int, Typeface> = HashMap()
        val headingFontStyleMap: HashMap<Int, Int> = HashMap()
        val headingColorsMap: HashMap<Int, Int> = HashMap()
        var paragraphTextSize: Int = 0
        var headingTypeface: Typeface? = null
        var headingTextSizes: FloatArray? = null
        var headingTextMargins: IntArray? = null
        var delimiterColor: Int = 0
        var delimiterBreakHeight = 1
        var tableColumnDrawableRes: Int = 0
        var tableColumnTextColor: Int = 0
        var imageBackground: Int = 0
        var imageBorder: Int = 0

        internal constructor()

        internal constructor(theme: EJStyle) {
            this.linkColor = theme.linkColor
            this.blockMargin = theme.blockPadding
            this.listItemColor = theme.listItemColor
            this.listBulletColor = theme.listBulletColor
            this.bulletDrawableRes = theme.bulletDrawable
            this.paragraphTextColor = theme.paragraphTextColor
            this.paragraphBackgroundColor = theme.paragraphBackgroundColor
            this.paragraphTypeface = theme.paragraphTypeface
            this.paragraphTextSize = theme.paragraphTextSize
            this.headingTypeface = theme.headingTypeface
            this.headingTextSizes = theme.headingTextSizeMultipliers
            this.headingTextMargins = theme.headingTextMarginsMultipliers
            this.delimiterColor = theme.delimiterColor
            this.delimiterBreakHeight = theme.delimiterHeight
            this.tableColumnTextColor = theme.tableColumnTextColor
            this.imageBackground = theme.imageBackground
            this.imageBorder = theme.imageBorder
            this.headingTextColor = theme.headingTextColor
        }

        fun linkColor(@ColorInt linkColor: Int): Builder {
            this.linkColor = linkColor
            return this
        }

        fun blockMargin(@Px blockMargin: Int): Builder {
            this.blockMargin = blockMargin
            return this
        }


        fun listItemColor(@ColorInt listItemColor: Int): Builder {
            this.listItemColor = listItemColor
            return this
        }

        fun listBulletColor(@ColorInt listItemColor: Int): Builder {
            this.listBulletColor = listItemColor
            return this
        }

        fun bulletDrawableRes(@DrawableRes drawableRes: Int): Builder {
            this.bulletDrawableRes = drawableRes
            return this
        }

        fun imageBackgroundRes(@DrawableRes imageBackground: Int): Builder {
            this.imageBackground = imageBackground
            return this
        }

        fun imageBorderRes(@DrawableRes imageBorder: Int): Builder {
            this.imageBorder = imageBorder
            return this
        }

        fun tableColumnDrawableRes(@DrawableRes drawableRes: Int): Builder {
            this.tableColumnDrawableRes = drawableRes
            return this
        }

        fun tableColumnTextColor(@ColorInt color: Int): Builder {
            this.tableColumnTextColor = color
            return this
        }

        fun paragraphTextColor(@ColorInt paragraphTextColor: Int): Builder {
            this.paragraphTextColor = paragraphTextColor
            return this
        }

        fun paragraphBackgroundColor(@ColorInt paragraphBackgroundColor: Int): Builder {
            this.paragraphBackgroundColor = paragraphBackgroundColor
            return this
        }

        fun paragraphTypeface(paragraphTypeface: Typeface): Builder {
            this.paragraphTypeface = paragraphTypeface
            return this
        }

        fun paragraphTextSize(@Px paragraphTextSize: Int): Builder {
            this.paragraphTextSize = paragraphTextSize
            return this
        }

        fun headingTypeface(headingTypeface: Typeface): Builder {
            this.headingTypeface = headingTypeface
            return this
        }

        fun headingTypefaceDetailed(headingTypeface: Typeface, level: HeadingLevel): Builder {
            this.headingTypefaceMap[level.value] = headingTypeface
            return this
        }

        fun headingColorDetailed(color: Int, level: HeadingLevel): Builder {
            this.headingColorsMap[level.value] = color
            return this
        }

        /**
         * @param headingTextSizes an array of multipliers values for heading elements.
         * The base value for this multipliers is TextView\'s text size
         */
        fun headingTextSizes(@Size(6) headingTextSizes: FloatArray): Builder {
            this.headingTextSizes = headingTextSizes
            return this
        }

        fun dividerBreakColor(@ColorInt thematicBreakColor: Int): Builder {
            this.delimiterColor = thematicBreakColor
            return this
        }

        fun dividerBreakHeight(thematicBreakHeight: Int): Builder {
            this.delimiterBreakHeight = thematicBreakHeight
            return this
        }

        fun build(): EJStyle {
            return EJStyle(this)
        }

        fun dividerMargin(marginLeft: Int,  marginTop: Int, marginRight: Int, marginBottom: Int): Builder {
            this.margins.setDeviderMargin(marginLeft, marginTop, marginRight, marginBottom)
            return this
        }

        fun headingMargin(marginLeft: Int,  marginTop: Int, marginRight: Int, marginBottom: Int, headerType: HeadingLevel): Builder {
            this.margins.setHeaderMargin(marginLeft, marginTop, marginRight, marginBottom, headerType)
            return this
        }

        fun imageMargin(marginLeft: Int,  marginTop: Int, marginRight: Int, marginBottom: Int): Builder {
            this.margins.setImageMargin(marginLeft, marginTop, marginRight, marginBottom)
            return this
        }

        fun paragraphMargin(marginLeft: Int,  marginTop: Int, marginRight: Int, marginBottom: Int): Builder {
            this.margins.setParagraphMargin(marginLeft, marginTop, marginRight, marginBottom)
            return this
        }

        fun rawHtmlMargin(marginLeft: Int,  marginTop: Int, marginRight: Int, marginBottom: Int): Builder {
            this.margins.setHtmlMargin(marginLeft, marginTop, marginRight, marginBottom)
            return this
        }

        fun tableMargin(marginLeft: Int,  marginTop: Int, marginRight: Int, marginBottom: Int): Builder {
            this.margins.setTableMargin(marginLeft, marginTop, marginRight, marginBottom)
            return this
        }

        fun listMargin(marginLeft: Int,  marginTop: Int, marginRight: Int, marginBottom: Int): Builder {
            this.margins.setListMargin(marginLeft, marginTop, marginRight, marginBottom)
            return this
        }

        fun headingFontStyleDetailed(style: Int, level: HeadingLevel): Builder {
            this.headingFontStyleMap[level.value] = style
            return this
        }
    }

    companion object {

        /**
         * Factory method to obtain an instance of [EJStyle] with all values as defaults
         *
         * @param context Context in order to resolve defaults
         * @return [EJStyle] instance
         * @see .builderWithDefaults
         * @since 1.0.0
         */
        fun create(context: Context): EJStyle {
            return builderWithDefaults(context).build()
        }

        /**
         * Create an **empty** instance of [Builder] with no default values applied
         */
        fun emptyBuilder(): Builder {
            return Builder()
        }

        /**
         * Factory method to create a [Builder] instance and initialize it with values
         * from supplied [EJStyle]
         *
         * @param copyFrom [EJStyle] to copy values from
         * @return [Builder] instance
         * @see .builderWithDefaults
         * @since 1.0.0
         */
        fun builder(copyFrom: EJStyle): Builder {
            return Builder(copyFrom)
        }

        /**
         * Factory method to obtain a [Builder] instance initialized with default values taken
         * from current application theme.
         *
         * @param context Context to obtain default styling values (colors, etc)
         * @return [Builder] instance
         * @since 1.0.0
         */
        fun builderWithDefaults(context: Context): Builder {
            return with(context) {
                Builder()
                    .bulletDrawableRes(R.drawable.list_circle)
                    .tableColumnDrawableRes(R.drawable.table_content_cell_bg)
                    .imageBackgroundRes(R.drawable.image_background)
            }
        }

        private val HEADING_SIZES = floatArrayOf(22f, 20f, 17f, 16f, 14f, 12f)
        private val HEADING_TOP_MARGINS_DEFAULT = intArrayOf(24, 32, 32, 14, 12, 8)
    }
}