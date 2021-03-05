package shattered.lib.gfx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import shattered.lib.Color;
import shattered.lib.math.Point;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;

final class PolygonBuilderImpl implements PolygonBuilder {

	private final TessellatorImpl tessellator;
	private final VertexArrayObject vao;
	private BufferBuilder builder = null;
	private boolean building = false;
	private Color mainColor = null;

	PolygonBuilderImpl(@NotNull final TessellatorImpl tessellator) {
		this.tessellator = tessellator;
		this.vao = VertexArrayObject.get();
	}

	@Override
	public void start(@Nullable final Color mainColor) {
		if (this.isBuilding()) {
			throw new IllegalStateException("Already building!");
		}
		this.builder = new BufferBuilder(GeneralVertexFormats.FORMAT_COLOR, GL_TRIANGLE_FAN, () -> {
			this.tessellator.getShader().bind();
			this.tessellator.getShader().setUniform1B("doTexture", false);
			final Matrix4f uniform = this.tessellator.getUniformMatrix();
			final Matrix4f matrix = MatrixUtils.identity();
			if (uniform != null) {
				matrix.mul(uniform);
			}
			this.tessellator.getShader().setUniformMat4("matCustom", matrix);
			this.vao.bind();
		});
		this.mainColor = mainColor;
		this.building = true;
	}

	@Override
	public void add(final int x, final int y) {
		this.add(x, y, null);
	}

	@Override
	public void add(final int x, final int y, @Nullable final Color pointColor) {
		this.add(Point.create(x, y), pointColor);
	}

	@Override
	public void add(@NotNull final Point position) {
		this.add(position, null);
	}

	@Override
	public void add(@NotNull final Point position, @Nullable final Color pointColor) {
		if (!this.isBuilding()) {
			throw new IllegalStateException("Not Building!");
		}
		if (this.mainColor == null && pointColor == null) {
			throw new IllegalStateException("No MainColor and PointColor provided!");
		}
		final Color color = pointColor != null ? pointColor : this.mainColor;
		this.builder.position(position.getX(), position.getY()).color(color).endVertex();
	}

	@Override
	public void draw() {
		if (!this.isBuilding()) {
			throw new IllegalStateException("Not Building!");
		}
		this.tessellator.getShader().setUniformMat4("matCustom", MatrixUtils.identity());
		this.builder.draw();
		this.building = false;
	}

	@Override
	public boolean isBuilding() {
		return this.building;
	}
}