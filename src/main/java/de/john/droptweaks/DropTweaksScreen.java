package de.john.droptweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DropTweaksScreen extends Screen {
    private static final String TITLE = "Johnˈs Drop Tweaks";
    private static final int CELL = 22;

    private record Entry(String id, String nameLower, ItemStack stack) {}
    private static List<Entry> ALL;

    private final Screen parent;
    private boolean designOpen = false, tweaksOpen = true;
    private EditBox search;
    private List<Entry> filtered = new ArrayList<>();
    private int scroll = 0;

    private int px, py, pw, ph;
    private int designHdrY, styleY, tweaksHdrY, searchY, btnY, listX, listY, listW, listH, cols;

    public DropTweaksScreen(Screen parent) {
        super(Component.literal(TITLE));
        this.parent = parent;
    }

    private static List<Entry> allEntries() {
        if (ALL == null) {
            List<Entry> l = new ArrayList<>();
            for (Item item : BuiltInRegistries.ITEM) {
                if (item == Items.AIR) continue;
                ItemStack st = new ItemStack(item);
                l.add(new Entry(BuiltInRegistries.ITEM.getKey(item).toString(),
                        st.getHoverName().getString().toLowerCase(Locale.ROOT), st));
            }
            l.sort((a, b) -> a.nameLower.compareTo(b.nameLower));
            ALL = l;
        }
        return ALL;
    }

    @Override
    protected void init() {
        pw = Math.min(360, width - 16);
        ph = Math.min(300, height - 16);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        String old = search == null ? "" : search.getValue();
        search = new EditBox(font, px + 10, 0, pw - 20, 16, Component.literal("Suche"));
        search.setHint(Component.literal("Item oder Block suchen..."));
        search.setMaxLength(60);
        search.setValue(old);
        search.setResponder(s -> filter());
        addWidget(search);
        filter();
        layout();
    }

    private void filter() {
        String q = search == null ? "" : search.getValue().trim().toLowerCase(Locale.ROOT);
        filtered = new ArrayList<>();
        for (Entry e : allEntries()) {
            if (q.isEmpty() || e.nameLower.contains(q) || e.id.contains(q)) filtered.add(e);
        }
        scroll = 0;
    }

    private void layout() {
        int y = py + 36;
        designHdrY = y; y += 18;
        if (designOpen) { styleY = y; y += 34; }
        y += 2;
        tweaksHdrY = y; y += 18;
        if (tweaksOpen) {
            searchY = y; y += 20;
            btnY = y; y += 20;
            listX = px + 10; listW = pw - 20;
            listY = y + 2;
            listH = Math.max(CELL, py + ph - 8 - listY);
            cols = Math.max(1, (listW - 8) / CELL);
            search.setY(searchY);
            search.setVisible(true);
            search.active = true;
        } else {
            search.setVisible(false);
            search.active = false;
        }
        clampScroll();
    }

    private int totalRows() { return (filtered.size() + cols - 1) / cols; }
    private int visibleRows() { return Math.max(1, listH / CELL); }
    private void clampScroll() { scroll = Math.max(0, Math.min(scroll, Math.max(0, totalRows() - visibleRows()))); }

    // ---------- Rendering ----------
    @Override public void renderBackground(GuiGraphics g, int mx, int my, float pt) { g.fill(0, 0, width, height, 0xA0000000); }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        renderBackground(g, mx, my, pt);
        Style s = Style.current();
        // Pulsierender Rahmen und ein dezenter Lichtstreifen sorgen für Bewegung,
        // ohne die Bedienung oder das ursprüngliche Layout zu verändern.
        double time = System.currentTimeMillis() / 1000.0;
        int pulse = (int) (24 + 22 * (0.5 + 0.5 * Math.sin(time * 2.4)));
        int glow = (pulse << 24) | (s.accent() & 0x00FFFFFF);
        g.fill(px - 3, py - 3, px + pw + 3, py + ph + 3, glow);
        g.fill(px - 1, py - 1, px + pw + 1, py + ph + 1, s.border());
        g.fill(px, py, px + pw, py + ph, s.bg());
        g.fillGradient(px, py, px + pw, py + 32, s.header(), s.bg());
        g.fill(px, py + 32, px + pw, py + 33, (s.accent() & 0x00FFFFFF) | 0xA0000000);
        int sweepX = px + (int) ((time * 34) % (pw + 40)) - 20;
        g.fillGradient(sweepX, py + 2, sweepX + 18, py + 30, 0x00111111, (pulse << 24) | (s.accent() & 0x00FFFFFF));
        drawTitle(g, px + 10, py + 10);

        drawHeader(g, "Design", designOpen, designHdrY, mx, my, s, null);
        if (designOpen) drawStyles(g, mx, my, s);
        drawHeader(g, "Drop Tweaks", tweaksOpen, tweaksHdrY, mx, my, s, Config.blocked.size() + " gesperrt");
        if (tweaksOpen) {
            search.render(g, mx, my, pt);
            drawButtons(g, mx, my);
            drawGrid(g, mx, my, s);
        }
    }

    /** Goldener, animierter Farbverlauf, schwarz umrandete Buchstaben. */
    private void drawTitle(GuiGraphics g, int x, int y) {
        float scale = 1.6f;
        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(scale, scale);
        double t = (System.currentTimeMillis() % 3000L) / 3000.0;
        int cx = 0;
        for (int i = 0; i < TITLE.length(); i++) {
            String c = String.valueOf(TITLE.charAt(i));
            double phase = (double) i / TITLE.length() - t;
            double k = 0.5 + 0.5 * Math.sin(phase * Math.PI * 2);   // 0..1
            int r = 255;
            int gr = (int) (170 + 85 * k);   // 170..255
            int b = (int) (20 + 120 * k * k);
            int color = 0xFF000000 | (r << 16) | (gr << 8) | b;
            for (int dx = -1; dx <= 1; dx++)
                for (int dy = -1; dy <= 1; dy++)
                    if (dx != 0 || dy != 0) g.drawString(font, c, cx + dx, dy, 0xFF000000, false);
            g.drawString(font, c, cx, 0, color, false);
            cx += font.width(c);
        }
        g.pose().popMatrix();
    }

    private void drawHeader(GuiGraphics g, String label, boolean open, int y, int mx, int my, Style s, String right) {
        boolean hov = mx >= px + 10 && mx < px + pw - 10 && my >= y && my < y + 16;
        g.fill(px + 10, y, px + pw - 10, y + 16, hov ? s.border() : s.header());
        int pulse = (int) (190 + 65 * (0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 260.0)));
        g.fill(px + 10, y, px + 13, y + 16, (pulse << 24) | (s.accent() & 0x00FFFFFF));
        g.drawString(font, (open ? "▼ " : "▶ ") + label, px + 18, y + 4, s.text(), false);
        if (right != null) g.drawString(font, right, px + pw - 16 - font.width(right), y + 4, s.accent(), false);
    }

    private int swatchW() { return (pw - 20 - 6 * 4) / Style.ALL.length; }

    private void drawStyles(GuiGraphics g, int mx, int my, Style cur) {
        int w = swatchW();
        for (int i = 0; i < Style.ALL.length; i++) {
            Style st = Style.ALL[i];
            int x = px + 10 + i * (w + 4);
            boolean sel = i == Config.style;
            boolean hov = mx >= x && mx < x + w && my >= styleY && my < styleY + 28;
            g.fill(x - 1, styleY - 1, x + w + 1, styleY + 29, sel ? 0xFFFFFFFF : hov ? 0xFFAAAAAA : st.border());
            g.fill(x, styleY, x + w, styleY + 28, st.bg());
            g.fill(x, styleY, x + w, styleY + 8, st.header());
            g.fill(x + 3, styleY + 12, x + w - 3, styleY + 16, st.accent());
            g.fill(x + 3, styleY + 20, x + w - 3, styleY + 24, st.cell());
            // Laufender Glanz auf dem gerade ausgewählten Design.
            if (sel) {
                int shimmerX = x + 2 + (int) ((System.currentTimeMillis() / 18L) % Math.max(1, w - 4));
                g.fill(shimmerX, styleY + 1, Math.min(x + w - 1, shimmerX + 3), styleY + 27, 0x55FFFFFF);
                g.fill(x + 1, styleY + 27, x + w - 1, styleY + 28, st.accent());
            }
            if (hov) g.renderTooltip(font, Component.literal(st.name()), mx, my);
        }
    }

    private void drawButtons(GuiGraphics g, int mx, int my) {
        int half = (pw - 20 - 6) / 2;
        int gx = px + 10, rx = px + 10 + half + 6;
        boolean hg = mx >= gx && mx < gx + half && my >= btnY && my < btnY + 16;
        boolean hr = mx >= rx && mx < rx + half && my >= btnY && my < btnY + 16;
        g.fill(gx, btnY, gx + half, btnY + 16, hg ? 0xFF3FD13F : 0xFF1F9A1F);
        g.fill(gx, btnY, gx + half, btnY + 1, 0x60FFFFFF);
        g.fill(rx, btnY, rx + half, btnY + 16, hr ? 0xFFEE4444 : 0xFFB02020);
        g.fill(rx, btnY, rx + half, btnY + 1, 0x60FFFFFF);
        String a = "Alles Auswählen", b = "Nichts Auswählen";
        g.drawString(font, a, gx + (half - font.width(a)) / 2, btnY + 4, 0xFFFFFFFF, true);
        g.drawString(font, b, rx + (half - font.width(b)) / 2, btnY + 4, 0xFFFFFFFF, true);
    }

    private int cellAt(double mx, double my) {
        if (!tweaksOpen || mx < listX || my < listY || mx >= listX + cols * CELL || my >= listY + visibleRows() * CELL) return -1;
        int col = (int) (mx - listX) / CELL;
        int row = (int) (my - listY) / CELL + scroll;
        int idx = row * cols + col;
        return idx >= 0 && idx < filtered.size() ? idx : -1;
    }

    private void drawGrid(GuiGraphics g, int mx, int my, Style s) {
        g.fill(listX - 1, listY - 1, listX + listW + 1, listY + visibleRows() * CELL + 1, s.border());
        g.fill(listX, listY, listX + listW, listY + visibleRows() * CELL, s.cell());
        int hover = cellAt(mx, my);
        g.enableScissor(listX, listY, listX + listW, listY + visibleRows() * CELL);
        for (int r = 0; r < visibleRows(); r++) {
            for (int c = 0; c < cols; c++) {
                int idx = (r + scroll) * cols + c;
                if (idx >= filtered.size()) break;
                Entry e = filtered.get(idx);
                int x = listX + c * CELL, y = listY + r * CELL;
                boolean sel = Config.blocked.contains(e.id);
                if (sel) {
                    g.fill(x + 1, y + 1, x + CELL - 1, y + CELL - 1, 0xAA2FBF2F);
                    g.fill(x + 1, y + 1, x + CELL - 1, y + 2, 0xFF7CFF7C);
                }
                if (idx == hover) g.fill(x + 1, y + 1, x + CELL - 1, y + CELL - 1, 0x50FFFFFF);
                g.renderItem(e.stack, x + 3, y + 3);
                if (sel) g.drawString(font, "✔", x + CELL - 8, y + CELL - 9, 0xFFFFFFFF, true);
            }
        }
        g.disableScissor();
        // Scrollbar
        int total = totalRows();
        if (total > visibleRows()) {
            int bx = listX + listW - 5, bh = visibleRows() * CELL;
            g.fill(bx, listY, bx + 4, listY + bh, 0x66000000);
            int th = Math.max(12, bh * visibleRows() / total);
            int ty = listY + (bh - th) * scroll / (total - visibleRows());
            g.fill(bx, ty, bx + 4, ty + th, s.accent());
        }
        if (filtered.isEmpty()) g.drawCenteredString(font, "Nichts gefunden", listX + listW / 2, listY + 10, 0xFFAAAAAA);
        if (hover >= 0) g.renderTooltip(font, filtered.get(hover).stack, mx, my);
    }

    // ---------- Input ----------
    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mx = click.x();
        double my = click.y();
        int button = click.button();
        if (button == 0) {
            if (inRect(mx, my, px + 10, designHdrY, pw - 20, 16)) { designOpen = !designOpen; layout(); return true; }
            if (inRect(mx, my, px + 10, tweaksHdrY, pw - 20, 16)) { tweaksOpen = !tweaksOpen; layout(); return true; }
            if (designOpen) {
                int w = swatchW();
                for (int i = 0; i < Style.ALL.length; i++)
                    if (inRect(mx, my, px + 10 + i * (w + 4), styleY, w, 28)) { Config.style = i; Config.save(); return true; }
            }
            if (tweaksOpen) {
                int half = (pw - 20 - 6) / 2;
                if (inRect(mx, my, px + 10, btnY, half, 16)) {
                    for (Entry e : allEntries()) Config.blocked.add(e.id);
                    Config.save(); return true;
                }
                if (inRect(mx, my, px + 10 + half + 6, btnY, half, 16)) {
                    Config.blocked.clear(); Config.save(); return true;
                }
                int idx = cellAt(mx, my);
                if (idx >= 0) {
                    String id = filtered.get(idx).id;
                    if (!Config.blocked.remove(id)) Config.blocked.add(id);
                    Config.save(); return true;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private static boolean inRect(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double sx, double sy) {
        if (tweaksOpen) { scroll -= (int) Math.signum(sy) * 2; clampScroll(); return true; }
        return super.mouseScrolled(mx, my, sx, sy);
    }

    @Override public void removed() { Config.save(); }
    @Override public void onClose() { Minecraft.getInstance().setScreen(parent); }
    @Override public boolean isPauseScreen() { return false; }
}
