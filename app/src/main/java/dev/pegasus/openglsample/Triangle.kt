package dev.pegasus.openglsample

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *   Developer: Sohaib Ahmed
 *   Date: 12/5/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class Triangle {

    // Vertex and fragment shaders define how the triangle is drawn
    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() { gl_Position = uMVPMatrix * vPosition; }
    """
    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 vColor;
        void main() { gl_FragColor = vColor; }
    """

    private val vertexBuffer: FloatBuffer
    private val program: Int
    private val color = floatArrayOf(0.6f, 0.7f, 0.2f, 1.0f) // Triangle color

    init {
        // Triangle vertex coordinates
        val coords = floatArrayOf(
            0f, 0.6f, 0f,   // Top
            -0.5f, -0.3f, 0f,  // Bottom-left
            0.5f, -0.3f, 0f   // Bottom-right
        )

        // Create buffer for vertices
        vertexBuffer = ByteBuffer.allocateDirect(coords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(coords)
                position(0)
            }
        }

        // Compile shaders and link the OpenGL program
        val vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }
    }

    // Draws the triangle using the provided transformation matrix
    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        // Get attribute and uniform locations
        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val colorHandle = GLES20.glGetUniformLocation(program, "vColor")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        // Pass vertex data
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        // Pass color and transformation matrix
        GLES20.glUniform4fv(colorHandle, 1, color, 0)
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3)
        GLES20.glDisableVertexAttribArray(positionHandle)
    }
}
