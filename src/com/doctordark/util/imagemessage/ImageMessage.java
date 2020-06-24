package com.doctordark.util.imagemessage;

import org.bukkit.entity.Player;
import java.util.Arrays;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.net.URL;
import java.awt.image.BufferedImage;
import org.bukkit.ChatColor;
import java.awt.Color;

public class ImageMessage
{
    private static final char TRANSPARENT_CHAR = ' ';
    private final String[] lines;
    private final Color[] colors;
    
    public ImageMessage(final String... imgLines) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        this.lines = imgLines;
    }
    
    public ImageMessage(final ChatColor[][] chatColors, final char imgChar) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        this.lines = this.toImgMessage(chatColors, imgChar);
    }
    
    public ImageMessage(final BufferedImage image, final int height, final char imgChar) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        this.lines = this.toImgMessage(this.toColourArray(image, height), imgChar);
    }
    
    public ImageMessage(final String url, final int height, final char imgChar) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        String[] result;
        try {
            final BufferedImage image = ImageIO.read(new URL(url));
            final ChatColor[][] colours = this.toColourArray(image, height);
            result = this.toImgMessage(colours, imgChar);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        this.lines = result;
    }
    
    public ImageMessage(final String fileName, final File folder, final int height, final char imgChar) {
        this.colors = new Color[] { new Color(0, 0, 0), new Color(0, 0, 170), new Color(0, 170, 0), new Color(0, 170, 170), new Color(170, 0, 0), new Color(170, 0, 170), new Color(255, 170, 0), new Color(170, 170, 170), new Color(85, 85, 85), new Color(85, 85, 255), new Color(85, 255, 85), new Color(85, 255, 255), new Color(255, 85, 85), new Color(255, 85, 255), new Color(255, 255, 85), new Color(255, 255, 255) };
        String[] result;
        try {
            final BufferedImage bufferedImage = ImageIO.read(new File(folder, fileName));
            final ChatColor[][] colours = this.toColourArray(bufferedImage, height);
            result = this.toImgMessage(colours, imgChar);
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
        this.lines = result;
    }
    
    public ImageMessage appendText(final String... text) {
        for (int y = 0; y < this.lines.length; ++y) {
            if (text.length > y) {
                final String[] lines = this.lines;
                lines[y] = lines[y] + ' ' + text[y];
            }
        }
        return this;
    }
    
    public ImageMessage appendCenteredText(final String... text) {
        for (int y = 0; y < this.lines.length; ++y) {
            if (text.length <= y) {
                return this;
            }
            final int len = 65 - this.lines[y].length();
            final StringBuilder sb = new StringBuilder();
            final String[] lines = this.lines;
            final int n = y;
            lines[n] = sb.append(lines[n]).append(this.center(text[y], len)).toString();
        }
        return this;
    }
    
    private ChatColor[][] toColourArray(final BufferedImage image, final int height) {
        final double ratio = image.getHeight() / image.getWidth();
        final BufferedImage reSized = this.resizeImage(image, (int)(height / ratio), height);
        final ChatColor[][] chatImg = new ChatColor[reSized.getWidth()][reSized.getHeight()];
        for (int x = 0; x < reSized.getWidth(); ++x) {
            for (int y = 0; y < reSized.getHeight(); ++y) {
                final int rgb = reSized.getRGB(x, y);
                final ChatColor closest = this.getClosestChatColor(new Color(rgb, true));
                chatImg[x][y] = closest;
            }
        }
        return chatImg;
    }
    
    private String[] toImgMessage(final ChatColor[][] colors, final char imgChar) {
        final String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; ++y) {
            final StringBuilder line = new StringBuilder();
            for (final ChatColor[] color1 : colors) {
                final ChatColor color2 = color1[y];
                line.append((color2 != null) ? (color1[y].toString() + imgChar) : ' ');
            }
            lines[y] = line.toString() + ChatColor.RESET;
        }
        return lines;
    }
    
    private BufferedImage resizeImage(final BufferedImage originalImage, final int width, final int height) {
        final AffineTransform af = new AffineTransform();
        af.scale(width / originalImage.getWidth(), height / originalImage.getHeight());
        final AffineTransformOp operation = new AffineTransformOp(af, 1);
        return operation.filter(originalImage, null);
    }
    
    private double getDistance(final Color c1, final Color c2) {
        final double redMean = (c1.getRed() + c2.getRed()) / 2.0;
        final double r = c1.getRed() - c2.getRed();
        final double g = c1.getGreen() - c2.getGreen();
        final int b = c1.getBlue() - c2.getBlue();
        final double weightR = 2.0 + redMean / 256.0;
        final double weightG = 4.0;
        final double weightB = 2.0 + (255.0 - redMean) / 256.0;
        return weightR * r * r + 4.0 * g * g + weightB * b * b;
    }
    
    private boolean areIdentical(final Color c1, final Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) <= 5 && Math.abs(c1.getGreen() - c2.getGreen()) <= 5 && Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
    }
    
    private ChatColor getClosestChatColor(final Color color) {
        if (color.getAlpha() < 128) {
            return null;
        }
        int index = 0;
        double best = -1.0;
        for (int i = 0; i < this.colors.length; ++i) {
            if (this.areIdentical(this.colors[i], color)) {
                return ChatColor.values()[i];
            }
        }
        for (int i = 0; i < this.colors.length; ++i) {
            final double distance = this.getDistance(color, this.colors[i]);
            if (distance < best || best == -1.0) {
                best = distance;
                index = i;
            }
        }
        return ChatColor.values()[index];
    }
    
    private String center(final String string, final int length) {
        if (string.length() > length) {
            return string.substring(0, length);
        }
        if (string.length() == length) {
            return string;
        }
        return Strings.repeat(' ', (length - string.length()) / 2) + string;
    }
    
    public String[] getLines() {
        return Arrays.<String>copyOf(this.lines, this.lines.length);
    }
    
    public void sendToPlayer(final Player player) {
        for (final String line : this.lines) {
            player.sendMessage(line);
        }
    }
}
