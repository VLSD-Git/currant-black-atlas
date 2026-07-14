from pathlib import Path

path = Path("app/src/main/assets/index.html")
text = path.read_text(encoding="utf-8")

text = text.replace(
    '<div><strong>Visualization Atlas</strong><span>Perception → inference → decisions</span></div>',
    '<div><strong>Pulse Atlas</strong><span>Atlas Noir Edition · Perception → decisions</span></div>',
)
text = text.replace(
    '<span class="eyebrow">Currant [Black] fieldbook edition</span>',
    '<span class="eyebrow">Pulse Atlas · Atlas Noir Edition</span>',
)
text = text.replace(
    'The <span class="glow">Quantitative</span><br>Visualization Atlas',
    'The <span class="glow">Pulse</span><br>Atlas',
)

css_anchor = '    @media(max-width:640px){.quote-gallery,.glossary-grid{grid-template-columns:1fr}.hero-art.currant-stage{min-height:380px}.eclipse{width:220px}.currant-orb.three{width:54px;height:54px}}\n\n'
css = r'''
    /* Pulse Atlas — Atlas Noir Edition */
    .nav-group{border:1px solid var(--line);border-radius:14px;background:rgba(255,255,255,.02);margin:.45rem 0;overflow:hidden}
    .nav-group summary{list-style:none;cursor:pointer;padding:.72rem .8rem;font-size:.76rem;font-weight:850;letter-spacing:.08em;text-transform:uppercase;color:var(--accent-2);display:flex;align-items:center;justify-content:space-between;background:linear-gradient(90deg,rgba(112,22,55,.16),rgba(255,255,255,.01))}
    .nav-group summary::-webkit-details-marker{display:none}.nav-group summary::after{content:"▾";transition:transform .25s ease}.nav-group:not([open]) summary::after{transform:rotate(-90deg)}
    .nav-links{display:grid;padding:.35rem .35rem .5rem}.nav-links a{margin:.08rem 0}
    .lux-card{margin:.7rem 0;padding:.75rem;border:1px solid rgba(255,193,104,.2);border-radius:15px;background:radial-gradient(circle at 85% 20%,rgba(255,173,68,.13),transparent 8rem),rgba(255,255,255,.016)}
    .lux-card strong{display:block;color:#ffd89c;font-size:.73rem;letter-spacing:.12em;text-transform:uppercase}.lux-card p{font-size:.74rem;color:var(--muted);margin:.3rem 0 0}
    @keyframes luxFloat{0%,100%{transform:translateY(0) rotate(-2deg)}50%{transform:translateY(-9px) rotate(2deg)}}
    @keyframes luxGlow{0%,100%{filter:drop-shadow(0 0 8px rgba(255,177,73,.45))}50%{filter:drop-shadow(0 0 20px rgba(255,194,102,.9))}}
    @keyframes luxWing{0%,100%{transform:rotate(-14deg) scaleY(1)}50%{transform:rotate(-4deg) scaleY(.82)}}
    @keyframes luxTrail{0%{opacity:0;transform:translate(0,0) scale(.7)}15%{opacity:.8}100%{opacity:0;transform:translate(-38px,28px) scale(1.15)}}
    @keyframes orbitDrift{0%,100%{transform:translateY(0)}50%{transform:translateY(-10px)}}
    @keyframes signalPulse{0%,100%{opacity:.78}50%{opacity:1}}
    .currant-orb.one{animation:orbitDrift 7s ease-in-out infinite}.currant-orb.two{animation:orbitDrift 5.8s ease-in-out infinite reverse}.currant-orb.three{animation:orbitDrift 8.4s ease-in-out infinite}.currant-orb.four{animation:orbitDrift 6.3s ease-in-out infinite reverse}.signal-wave{animation:signalPulse 3.8s ease-in-out infinite}
    .lux-companion{position:absolute;right:8%;bottom:12%;width:138px;height:112px;z-index:6;animation:luxFloat 4.2s ease-in-out infinite;filter:drop-shadow(0 12px 25px rgba(0,0,0,.42))}
    .lux-body{position:absolute;left:55px;top:37px;width:29px;height:43px;border-radius:60% 60% 55% 55%;background:linear-gradient(180deg,#1a101e,#08050a 46%,#ffbd61 65%,#fff0ae);animation:luxGlow 2.8s ease-in-out infinite}
    .lux-head{position:absolute;left:52px;top:25px;width:33px;height:27px;border-radius:50%;background:radial-gradient(circle at 40% 32%,#83718f 0 7%,#1a101f 25%,#050307 75%)}
    .lux-eye{position:absolute;left:61px;top:34px;width:7px;height:8px;border-radius:50%;background:#fff3d3;box-shadow:0 0 9px #ffc86e}
    .lux-wing{position:absolute;top:25px;width:47px;height:64px;border-radius:70% 22% 70% 24%;border:1px solid rgba(255,216,158,.48);background:linear-gradient(145deg,rgba(255,225,178,.24),rgba(168,75,255,.08));transform-origin:bottom center;backdrop-filter:blur(2px)}
    .lux-wing.left{left:17px;animation:luxWing .7s ease-in-out infinite}.lux-wing.right{right:14px;transform:scaleX(-1) rotate(-14deg);animation:luxWing .7s ease-in-out infinite reverse}
    .lux-antenna,.lux-antenna.two{position:absolute;top:9px;width:2px;height:28px;background:linear-gradient(#ffd28b,transparent);transform-origin:bottom}.lux-antenna{left:57px;transform:rotate(-25deg)}.lux-antenna.two{left:78px;transform:rotate(24deg)}
    .lux-spark{position:absolute;left:47px;bottom:10px;width:8px;height:8px;border-radius:50%;background:#ffd77e;box-shadow:0 0 15px #ffb84e;animation:luxTrail 2.8s linear infinite}.lux-spark.s2{animation-delay:.9s}.lux-spark.s3{animation-delay:1.8s}
    .lux-bubble{position:absolute;right:118px;bottom:72px;width:145px;padding:.58rem .7rem;border:1px solid rgba(255,209,139,.18);border-radius:14px 14px 4px 14px;background:rgba(9,4,12,.91);color:#f7e8f4;font-size:.7rem;line-height:1.35;box-shadow:0 12px 32px rgba(0,0,0,.3)}.lux-bubble strong{display:block;color:#ffd393;text-transform:uppercase;letter-spacing:.14em;font-size:.62rem;margin-bottom:.18rem}
    .lux-label{position:absolute;left:48px;bottom:-10px;color:rgba(255,221,170,.72);font-size:.62rem;letter-spacing:.18em;text-transform:uppercase}
    @media(max-width:760px){.lux-companion{right:1%;bottom:4%;transform:scale(.86);transform-origin:bottom right}.lux-bubble{right:100px;width:118px;font-size:.64rem}}
'''
if css_anchor in text and "Pulse Atlas — Atlas Noir Edition" not in text:
    text = text.replace(css_anchor, css_anchor + css, 1)
elif ".lux-companion{" not in text:
    text = text.replace("  </style>", css + "\n  </style>", 1)

orbs = '<i class="currant-orb one"></i><i class="currant-orb two"></i><i class="currant-orb three"></i><i class="currant-orb four"></i>'
lux = orbs + '''<div class="lux-companion" aria-hidden="true"><div class="lux-bubble"><strong>Lux recommends</strong>A pattern is waiting. Look closer.</div><i class="lux-wing left"></i><i class="lux-wing right"></i><i class="lux-antenna"></i><i class="lux-antenna two"></i><i class="lux-head"></i><i class="lux-eye"></i><i class="lux-body"></i><i class="lux-spark"></i><i class="lux-spark s2"></i><i class="lux-spark s3"></i><span class="lux-label">Lux</span></div>'''
if "lux-companion" not in text.split("<body",1)[-1]:
    text = text.replace(orbs, lux, 1)

old_start = '''  let html="", navhtml='<h3>Start</h3><a href="#home"><span class="dot"></span>Overview</a>';
  chapters.forEach((ch,ci)=>{
    navhtml += `<h3>${ch.title}</h3>`;
    ch.concepts.forEach(c=> navhtml += `<a href="#${c.id}" data-target="${c.id}"><span class="dot"></span>${c.title}</a>`);
    html += `<section class="chapter" id="${ch.id}">'''
new_start = '''  let html="";
  let navhtml=`<div class="lux-card"><strong>Lux is with you</strong><p>Open a chapter, follow a signal, or continue where you left off.</p></div>`;
  navhtml+=`<details class="nav-group" open><summary>Start here</summary><div class="nav-links"><a href="#home"><span class="dot"></span>Overview</a><a href="#learning-paths"><span class="dot"></span>Learning Paths</a><a href="#clinic"><span class="dot"></span>Visualization Clinic</a></div></details>`;
  chapters.forEach((ch,ci)=>{
    const chapterLinks=ch.concepts.map(c=>`<a href="#${c.id}" data-target="${c.id}"><span class="dot"></span>${c.title}</a>`).join("");
    navhtml+=`<details class="nav-group" ${ci<2?"open":""}><summary>Chapter ${ci+1} · ${ch.title}</summary><div class="nav-links">${chapterLinks}</div></details>`;
    html += `<section class="chapter" id="${ch.id}">'''
text = text.replace(old_start, new_start)

old_tail = '''  navhtml += `<h3>Apprenticeship</h3><a href="#learning-paths"><span class="dot"></span>Learning Paths</a><a href="#clinic"><span class="dot"></span>Visualization Clinic</a><a href="#chart-studio"><span class="dot"></span>Build Studio</a><a href="#applications"><span class="dot"></span>Applied Portfolio</a><a href="#museum"><span class="dot"></span>Consequential Museum</a><a href="#reading-companions"><span class="dot"></span>Reading Companions</a><a href="#honesty-lab"><span class="dot"></span>Honesty Lab</a><h3>Study tools</h3><a href="#visual-lab"><span class="dot"></span>Visual Lab</a><a href="#challenges"><span class="dot"></span>Challenges & Review</a><a href="#fieldbook"><span class="dot"></span>Personal Fieldbook</a><a href="#pattern-gallery"><span class="dot"></span>Pattern Gallery</a><a href="#knowledge-check"><span class="dot"></span>Knowledge Check</a><a href="#canon"><span class="dot"></span>Canon Quotes</a><a href="#glossary"><span class="dot"></span>Glossary</a><a href="#references"><span class="dot"></span>Reading Map</a>`;'''
new_tail = '''  navhtml += `<details class="nav-group" open><summary>Studios & practice</summary><div class="nav-links"><a href="#chart-studio"><span class="dot"></span>Build Studio</a><a href="#applications"><span class="dot"></span>Applied Portfolio</a><a href="#museum"><span class="dot"></span>Consequential Museum</a><a href="#reading-companions"><span class="dot"></span>Reading Companions</a><a href="#honesty-lab"><span class="dot"></span>Honesty Lab</a><a href="#visual-lab"><span class="dot"></span>Visual Lab</a><a href="#challenges"><span class="dot"></span>Challenges & Review</a><a href="#fieldbook"><span class="dot"></span>Nzo’s Fieldbook</a><a href="#pattern-gallery"><span class="dot"></span>Pattern Gallery</a><a href="#knowledge-check"><span class="dot"></span>Knowledge Check</a><a href="#canon"><span class="dot"></span>Canon Quotes</a><a href="#glossary"><span class="dot"></span>Glossary</a><a href="#references"><span class="dot"></span>Reading Map</a></div></details>`;'''
text = text.replace(old_tail, new_tail)
text = text.replace("Personal Fieldbook", "Nzo’s Fieldbook")

path.write_text(text, encoding="utf-8")
print("Pulse Atlas v1.0.2 UI applied")
