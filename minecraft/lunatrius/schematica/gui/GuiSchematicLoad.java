package lunatrius.schematica.gui;

import lunatrius.schematica.FileFilterSchematic;
import lunatrius.schematica.Settings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSmallButton;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import org.lwjgl.Sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GuiSchematicLoad extends GuiScreen {
	private static final FileFilterSchematic FILE_FILTER_FOLDER = new FileFilterSchematic(true);
	private static final FileFilterSchematic FILE_FILTER_SCHEMATIC = new FileFilterSchematic(false);

	private final Settings settings = Settings.instance();
	private final GuiScreen prevGuiScreen;
	private GuiSchematicLoadSlot guiSchematicLoadSlot;

	private GuiSmallButton btnOpenDir = null;
	private GuiSmallButton btnDone = null;

	private final String strTitle = StatCollector.translateToLocal("schematica.gui.title");
	private final String strFolderInfo = StatCollector.translateToLocal("schematica.gui.folderInfo");

	protected File currentDirectory = this.settings.schematicDirectory;
	protected final List<GuiSchematicEntry> schematicFiles = new ArrayList<GuiSchematicEntry>();

	public GuiSchematicLoad(GuiScreen guiScreen) {
		this.prevGuiScreen = guiScreen;
	}

	@Override
	public void initGui() {
		int id = 0;

		this.btnOpenDir = new GuiSmallButton(id++, this.width / 2 - 154, this.height - 36, StatCollector.translateToLocal("schematica.gui.openFolder"));
		this.buttonList.add(this.btnOpenDir);

		this.btnDone = new GuiSmallButton(id++, this.width / 2 + 4, this.height - 36, StatCollector.translateToLocal("schematica.gui.done"));
		this.buttonList.add(this.btnDone);

		this.guiSchematicLoadSlot = new GuiSchematicLoadSlot(this);

		reloadSchematics();
	}

	@Override
	protected void actionPerformed(GuiButton guiButton) {
		if (guiButton.enabled) {
			if (guiButton.id == this.btnOpenDir.id) {
				boolean success = false;

				try {
					Class c = Class.forName("java.awt.Desktop");
					Object m = c.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
					c.getMethod("browse", new Class[] {
							URI.class
					}).invoke(m, new Object[] {
							Settings.schematicDirectory.toURI()
					});
				} catch (Throwable e) {
					success = true;
				}

				if (success) {
					Settings.logger.logInfo("Opening via Sys class!");
					Sys.openURL("file://" + Settings.schematicDirectory.getAbsolutePath());
				}
			} else if (guiButton.id == this.btnDone.id) {
				loadSchematic();
				this.mc.displayGuiScreen(this.prevGuiScreen);
			} else {
				this.guiSchematicLoadSlot.actionPerformed(guiButton);
			}
		}
	}

	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		this.guiSchematicLoadSlot.drawScreen(x, y, partialTicks);

		drawCenteredString(this.fontRenderer, this.strTitle, this.width / 2, 4, 0x00FFFFFF);
		drawCenteredString(this.fontRenderer, this.strFolderInfo, this.width / 2 - 78, this.height - 12, 0x00808080);

		super.drawScreen(x, y, partialTicks);
	}

	@Override
	public void onGuiClosed() {
		// loadSchematic();
	}

	protected void changeDirectory(String directory) {
		this.currentDirectory = new File(this.currentDirectory, directory);

		reloadSchematics();
	}

	protected void reloadSchematics() {
		String name = null;
		int itemID = -1;

		this.schematicFiles.clear();

		try {
			if (!this.currentDirectory.getCanonicalPath().equals(Settings.schematicDirectory.getCanonicalPath())) {
				this.schematicFiles.add(new GuiSchematicEntry("..", 327, 0, true));
			}
		} catch (IOException e) {
			Settings.logger.logSevereException("Failed to add GuiSchematicEntry!", e);
		}

		for (File file : this.currentDirectory.listFiles(FILE_FILTER_FOLDER)) {
			name = file.getName();

			itemID = file.listFiles().length == 0 ? 325 : 326;

			this.schematicFiles.add(new GuiSchematicEntry(name, itemID, 0, file.isDirectory()));
		}

		File[] files = this.currentDirectory.listFiles(FILE_FILTER_SCHEMATIC);
		if (files.length == 0) {
			this.schematicFiles.add(new GuiSchematicEntry(StatCollector.translateToLocal("schematica.gui.noschematic"), 3, 0, false));
		} else {
			for (File file : files) {
				name = file.getName();

				this.schematicFiles.add(new GuiSchematicEntry(name, readSchematicIcon(file.getAbsolutePath()), file.isDirectory()));
			}
		}
	}

	private ItemStack readSchematicIcon(String filename) {
		try {
			InputStream stream = new FileInputStream(filename);
			NBTTagCompound tagCompound = CompressedStreamTools.readCompressed(stream);

			if (tagCompound != null) {
				if (tagCompound.hasKey("Icon")) {
					ItemStack itemStack = Settings.defaultIcon.copy();
					itemStack.readFromNBT(tagCompound.getCompoundTag("Icon"));
					return itemStack;
				}
			}
		} catch (Exception e) {
			Settings.logger.logSevereException("Failed to read schematic icon!", e);
		}

		return Settings.defaultIcon.copy();
	}

	private void loadSchematic() {
		int selectedIndex = this.guiSchematicLoadSlot.selectedIndex;

		//2013-09-04: added some logging to figure out if OpenGL errors are due to different thread
		//Settings.logger.logSevereException( Thread.currentThread().getName() + " - " + Thread.currentThread().getId(), new RuntimeException("GuiSchematicLoad") );
		Settings.logger.logInfo( "Workbench thread: " + Thread.currentThread().getName() + " - " + Thread.currentThread().getId() );
		try {
			if (selectedIndex >= 0 && selectedIndex < this.schematicFiles.size()) {
				GuiSchematicEntry schematic = this.schematicFiles.get(selectedIndex);
				this.settings.loadSchematic((new File(this.currentDirectory, schematic.getName())).getCanonicalPath());
			}
		} catch (Exception e) {
			Settings.logger.logSevereException("Failed to load schematic!", e);
		}
		this.settings.moveHere();
	}
}
