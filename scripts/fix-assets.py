import os
import shutil

root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
brand = os.path.join(root, 'src', 'assets', 'brand')
insta = os.path.join(root, 'src', 'assets', 'instagram')
loja = os.path.join(root, 'src', 'assets', 'loja')

if not os.path.isdir(brand) or not os.path.isdir(insta) or not os.path.isdir(loja):
    raise SystemExit('Assets directories missing')

# Ensure a stable brand logo name
brand_files = [f for f in os.listdir(brand) if f.lower().endswith(('.jpg', '.jpeg', '.png', '.webp'))]
if brand_files:
    src = os.path.join(brand, brand_files[0])
    dst = os.path.join(brand, 'logo.png')
    if src != dst:
        if os.path.exists(dst):
            print('brand logo target exists:', dst)
        else:
            shutil.copyfile(src, dst)
            print('Copied brand logo to', dst)

# Ensure hero image exists
hero_dst = os.path.join(insta, 'hero.jpg')
if not os.path.exists(hero_dst):
    for f in os.listdir(insta):
        if f.lower().endswith(('.jpg', '.jpeg', '.png')):
            src = os.path.join(insta, f)
            shutil.copyfile(src, hero_dst)
            print('Copied hero image from', f)
            break
    else:
        print('No suitable hero image found in instagram assets')

# Ensure default product fallback exists
fallback = os.path.join(loja, 'default-product.webp')
if not os.path.exists(fallback):
    webps = [f for f in os.listdir(loja) if f.lower().endswith('.webp')]
    if webps:
        shutil.copyfile(os.path.join(loja, webps[0]), fallback)
        print('Copied default loja product from', webps[0])
    else:
        print('No webp product image found in loja assets')
