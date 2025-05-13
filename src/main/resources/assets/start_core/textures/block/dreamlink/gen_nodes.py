import numpy as np
import os
from PIL import Image
from collections import deque
import shutil

def generate_ripple_spritesheet(
    size=(16, 16),
    gradient_colors=[(255, 200, 200), (200, 220, 255)],
    n_frames=16,
    frames_per_row=1,
    wave_count=4
):
    """
    Generate a spritesheet of frames depicting a radial ripple animation.

    Returns
    -------
    sheet : PIL.Image
        The assembled RGB spritesheet image.
    frames : list of PIL.Image
        Individual frame images.
    """
    width, height = size
    cols = frames_per_row
    rows = (n_frames + cols - 1) // cols

    sheet = Image.new('RGB', (width * cols, height * rows))

    xs = np.linspace(-1.0, 1.0, width)
    ys = np.linspace(-1.0, 1.0, height)
    xv, yv = np.meshgrid(xs, ys)
    radius = np.sqrt(xv**2 + yv**2)

    def lerp(a, b, t):
        return a + (b - a) * t

    frames = []
    for i in range(n_frames):
        phase = 2 * np.pi * (i / n_frames)
        wave = np.sin(radius * wave_count * 2 * np.pi - phase)
        t = (wave + 1.0) / 2.0

        c0 = np.array(gradient_colors[0], dtype=np.float32)
        c1 = np.array(gradient_colors[1], dtype=np.float32)
        arr = np.zeros((height, width, 3), dtype=np.uint8)
        for ch in range(3):
            arr[..., ch] = np.clip(lerp(c0[ch], c1[ch], t), 0, 255).astype(np.uint8)

        frame = Image.fromarray(arr)
        frames.append(frame)

        x = (i % cols) * width
        y = (i // cols) * height
        sheet.paste(frame, (x, y))

    return sheet, frames


def generate_overlayed_sheet(
    ripple_frames,
    overlay_path,
    output_folder,
    output_name,
    overlay_index=4,
    size=(16, 16),
    frames_per_row=1,
    outline=True
):
    width, height = size
    overlay_img = Image.open(overlay_path).convert('RGBA')
    overlay = overlay_img.crop((0, overlay_index * height, width, (overlay_index + 1) * height))

    data = np.array(overlay)
    orig_alpha = data[..., 3].copy()
    mask_black = np.all(data[..., :3] == [0, 0, 0], axis=-1)
    h, w = mask_black.shape

    background = np.zeros_like(mask_black, dtype=bool)
    dq = deque()
    for x in range(w):
        if mask_black[0, x]: dq.append((0, x)); background[0, x] = True
        if mask_black[h - 1, x]: dq.append((h - 1, x)); background[h - 1, x] = True
    for y in range(h):
        if mask_black[y, 0]: dq.append((y, 0)); background[y, 0] = True
        if mask_black[y, w - 1]: dq.append((y, w - 1)); background[y, w - 1] = True
    while dq:
        y, x = dq.popleft()
        for dy, dx in [(-1, 0), (1, 0), (0, -1), (0, 1)]:
            ny, nx = y + dy, x + dx
            if 0 <= ny < h and 0 <= nx < w and mask_black[ny, nx] and not background[ny, nx]:
                background[ny, nx] = True
                dq.append((ny, nx))

    data[background, 3] = 0
    overlay_sprite = Image.fromarray(data)
    interior_mask = (orig_alpha == 0)
    mask_img = Image.fromarray((interior_mask.astype(np.uint8) * 255), mode='L')

    combined = []
    for ripple in ripple_frames:
        frame = Image.new('RGBA', (width, height), (0, 0, 0, 0))
        ripple_rgba = ripple.convert('RGBA')
        frame.paste(ripple_rgba, (0, 0), mask_img)
        if outline:
            frame.paste(overlay_sprite, (0, 0), overlay_sprite)
        combined.append(frame)

    cols = frames_per_row
    rows = (len(combined) + cols - 1) // cols
    sheet = Image.new('RGBA', (width * cols, height * rows), (0, 0, 0, 0))
    for i, frm in enumerate(combined):
        x = (i % cols) * width
        y = (i // cols) * height
        sheet.paste(frm, (x, y), frm)

    os.makedirs(output_folder, exist_ok=True)
    path = os.path.join(output_folder, output_name)

    shutil.copyfile("overlay_front_emissive.png.mcmeta", output_folder + "/overlay_front_emissive.png.mcmeta")
    shutil.copyfile("overlay_front.png.mcmeta", output_folder + "/overlay_front.png.mcmeta")
    print(f"Saved: {output_folder + '/overlay_front.png.mcmeta'}")
    sheet.save(path)
    print(f"Saved: {path}")


if __name__ == '__main__':
    refined_palettes = {
        "dream_link_node": [
            (251, 232, 242),  # lighter soft pink
            (214, 160, 185),  # richer pink-mauve
        ],
        "oneiric_relay": [
            (255, 243, 216),  # lighter peach
            (220, 180, 130),  # deeper peachy ochre
        ],
        "daydream_spire": [
            (234, 248, 224),  # lighter pale green
            (190, 210, 160),  # deeper leaf-sage
        ],
        "beacon_of_lucidity": [
            (248, 230, 255),  # lighter lilac
            (180, 190, 220),  # muted periwinkle-turquoise
        ],
        "paragon_of_the_veil": [
            (230, 245, 255),  # lighter sky blue
            (150, 190, 230),  # deeper soft azure
        ],
    }

    for folder, palette in refined_palettes.items():
        ripple_sheet, ripple_frames = generate_ripple_spritesheet(
            size=(16, 16),
            gradient_colors=palette,
            n_frames=16,
            frames_per_row=1,
            wave_count=4
        )

        generate_overlayed_sheet(
            ripple_frames,
            overlay_path='hatch_overlay_base.png',
            output_folder=folder,
            output_name='overlay_front.png',
            overlay_index=4,
            size=(16, 16),
            frames_per_row=1
        )

        generate_overlayed_sheet(
            ripple_frames,
            overlay_path='hatch_overlay_base.png',
            output_folder=folder,
            output_name='overlay_front_emissive.png',
            overlay_index=4,
            size=(16, 16),
            frames_per_row=1,
            outline=False
        )
