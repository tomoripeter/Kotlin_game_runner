import javafx.scene.canvas.GraphicsContext

interface IDrawable {
    val posX: Double
    val posY: Double

    fun draw(gc: GraphicsContext, time: Double, gameOver: Boolean)
    fun calculatePos(keyboardInputs: ArrayList<String>)
}