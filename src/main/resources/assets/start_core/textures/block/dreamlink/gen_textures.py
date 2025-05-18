import numpy as np
import os
from PIL import Image
from collections import deque
import shutil

def generate_swirl_spritesheet(
    size=(16, 16),
    gradient_colors=[(255, 0, 0), (0, 0, 255)],
    n_frames=16,
    frames_per_row=8,
    swirl_strength=3.0
):
    """
    Generate a spritesheet of frames depicting a swirling gradient.

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
    base_angle = np.arctan2(yv, xv)

    def lerp(a, b, t):
        return a + (b - a) * t

    frames = []
    for i in range(n_frames):
        rotation = 2 * np.pi * (i / n_frames)
        angle = base_angle + swirl_strength * radius + rotation
        t = (angle / (2 * np.pi)) % 1.0

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


def generate_overlayed_sheets(
    swirl_frames,
    overlay_path,
    tier,
    output,
    size=(16,16),
    frames_per_row=8,
    outline=True
):
    """
    For each sprite in a vertical overlay sheet, overlay it across all swirl frames
    so only the interior of the outline shows the swirl; outside remains transparent.
    """
    width, height = size
    overlay_img = Image.open(overlay_path).convert('RGBA')
    n_overlays = overlay_img.height // height

    # Split into individual overlay sprites
    overlays = [overlay_img.crop((0, i*height, width, (i+1)*height))
                for i in range(n_overlays)]

    # Process each overlay: compute background transparency and interior mask
    processed = []
    for o in overlays:
        data = np.array(o)
        orig_alpha = data[..., 3].copy()
        # Identify all black pixels
        mask_black = np.all(data[..., :3] == [0, 0, 0], axis=-1)
        h, w = mask_black.shape

        # Flood-fill from borders to find background region
        background = np.zeros_like(mask_black, dtype=bool)
        dq = deque()
        # enqueue border black pixels
        for x in range(w):
            if mask_black[0, x]: dq.append((0, x)); background[0, x] = True
            if mask_black[h-1, x]: dq.append((h-1, x)); background[h-1, x] = True
        for y in range(h):
            if mask_black[y, 0]: dq.append((y, 0)); background[y, 0] = True
            if mask_black[y, w-1]: dq.append((y, w-1)); background[y, w-1] = True
        # flood-fill
        while dq:
            y, x = dq.popleft()
            for dy, dx in [(-1,0),(1,0),(0,-1),(0,1)]:
                ny, nx = y + dy, x + dx
                if 0 <= ny < h and 0 <= nx < w and mask_black[ny, nx] and not background[ny, nx]:
                    background[ny, nx] = True
                    dq.append((ny, nx))

        # Make outside black region transparent
        data[background, 3] = 0
        overlay_sprite = Image.fromarray(data)

        # Interior mask: originally transparent region
        interior_mask = (orig_alpha == 0)
        mask_img = Image.fromarray((interior_mask.astype(np.uint8) * 255), mode='L')

        processed.append((overlay_sprite, mask_img))

    # For each overlay index, composite swirl inside only interior region and outline on top
    for idx, (overlay_sprite, mask_img) in enumerate(processed):
        combined = []
        for swirl in swirl_frames:
            # blank RGBA canvas
            frame = Image.new('RGBA', (width, height), (0, 0, 0, 0))
            swirl_rgba = swirl.convert('RGBA')
            # paste swirl only where interior mask True
            frame.paste(swirl_rgba, (0, 0), mask_img)

            if outline:
                # paste outline (overlay_sprite has transparent outside/interior)
                frame.paste(overlay_sprite, (0, 0), overlay_sprite)
            combined.append(frame)

        # Assemble spritesheet
        cols = frames_per_row
        rows = (len(combined) + cols - 1) // cols
        sheet = Image.new('RGBA', (width * cols, height * rows), (0, 0, 0, 0))
        for i, frm in enumerate(combined):
            x = (i % cols) * width
            y = (i // cols) * height
            sheet.paste(frm, (x, y), frm)

        amps = {
            7: "2",
            6: "4",
            5: "16",
            4: "64",
            3: "256",
            2: "1024",
            1: "4096",
            0: "16384"
        }

        filename = f"{tier}_{amps[idx]}a_energy_hatch/{output}"
        if not os.path.exists(os.path.dirname(filename)):
            os.makedirs(os.path.dirname(filename))


        if output == "overlay_front.png":
            if (idx == 7):
                sheet.save(f"../../item/{tier}_{amps[idx]}a_dream_link_cover_item.png")
                shutil.copyfile("overlay_front.png.mcmeta", f"../../item/{tier}_{amps[idx]}a_dream_link_cover_item.png.mcmeta")

        shutil.copyfile("overlay_front_emissive.png.mcmeta", os.path.dirname(filename) + "/overlay_front_emissive.png.mcmeta")
        shutil.copyfile("overlay_front.png.mcmeta", os.path.dirname(filename) + "/overlay_front.png.mcmeta")

        sheet.save(filename)
        print(f"Saved: {filename}")


if __name__ == '__main__':
    gradient_tiers = {
        "uv":  [(126, 176, 126), (186, 236, 196)],    # fresh green to soft mint
        "uhv": [(191, 116, 192), (241, 166, 242)],    # vibrant lavender shifting to light pinkish violet
        "uev": [(11, 92, 254),  (91, 162, 254)],      # strong blue softening into sky blue (already good)
        "uiv": [(145, 78, 145), (205, 108, 195)],     # muted purple shifting to warm soft pink-purple
        "uxv": [(72, 135, 72),  (152, 195, 132)],     # earthy green moving into light moss green
        "opv": [(140, 0, 0),    (220, 80, 80)],       # deep red to warm rose (already good)
        "max": [(40, 40, 245),  (100, 100, 255)],     # deep blue shifting to light royal blue (already good)
    }

    for tier, gradient in gradient_tiers.items():
        swirl_sheet, swirl_frames = generate_swirl_spritesheet(
            size=(16, 16),
            gradient_colors=gradient,
            n_frames=16,
            frames_per_row=1,
            swirl_strength=4.0
        )
        swirl_sheet.save('swirl_spritesheet.png')

        generate_overlayed_sheets(
            swirl_frames,
            overlay_path='hatch_overlay_base.png',
            tier=tier,
            output="overlay_front.png",
            size=(16, 16),
            frames_per_row=1,
        )

        generate_overlayed_sheets(
            swirl_frames,
            overlay_path='hatch_overlay_base.png',
            tier=tier,
            output="overlay_front_emissive.png",
            size=(16, 16),
            frames_per_row=1,
            outline=False
        )