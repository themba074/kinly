const $=(s,c=document)=>c.querySelector(s);const $$=(s,c=document)=>[...c.querySelectorAll(s)];
const people={themba:{name:'Themba',status:'Driving · 42 km/h'},nandi:{name:'Nandi',status:'At home'},lwazi:{name:'Lwazi',status:'At school'},zola:{name:'Zola',status:'Battery saver'}};
const toast=(title,detail='')=>{const t=$('#toast');t.querySelector('strong').textContent=title;t.querySelector('small').textContent=detail;t.classList.add('show');clearTimeout(window.toastTimer);window.toastTimer=setTimeout(()=>t.classList.remove('show'),3200)};
function selectPerson(id){$$('.member').forEach(x=>x.classList.toggle('selected',x.dataset.person===id));$$('.person-pin').forEach(x=>x.classList.toggle('active',x.dataset.person===id));const p=people[id];toast(`${p.name} selected`,p.status)}
$$('[data-person]').forEach(el=>el.addEventListener('click',()=>selectPerson(el.dataset.person)));
$('#checkinBtn').addEventListener('click',()=>toast('Check-in sent','Your circle can reply with one tap.'));
$('#locateBtn').addEventListener('click',()=>{const b=$('.pin-me');b.animate([{transform:'scale(1)'},{transform:'scale(1.18)'},{transform:'scale(1)'}],{duration:650});toast('Map centred','Showing your current location.')});
$$('.privacy-option').forEach(btn=>btn.addEventListener('click',()=>{$$('.privacy-option').forEach(x=>x.classList.remove('active'));btn.classList.add('active');const copy={precise:['Precise sharing on','Your circle can see your live location.'],approx:['Approximate sharing on','Only your neighbourhood is visible.'],paused:['Location paused','You are private for the next hour.']}[btn.dataset.mode];toast(...copy)}));
$$('.bottom-nav button[data-tab]').forEach(btn=>btn.addEventListener('click',()=>{$$('.bottom-nav button[data-tab]').forEach(x=>x.classList.remove('active'));btn.classList.add('active');if(btn.dataset.tab!=='home')toast(`${btn.textContent.trim()} is ready`,'This prototype keeps you on the main dashboard.')}));
const modal=$('#modal');function showModal({icon='✦',title,text,action='Continue',input=true}){$('#modalIcon').textContent=icon;$('#modalTitle').textContent=title;$('#modalText').textContent=text;$('#modalAction').textContent=action;$('#inviteInput').style.display=input?'block':'none';modal.showModal()}
$('#inviteBtn').addEventListener('click',()=>showModal({title:'Invite someone you trust',text:'Add a person to your private circle. They’ll choose exactly what they share.',action:'Send invite'}));
$('#sosBtn').addEventListener('click',()=>showModal({icon:'＋',title:'Emergency help',text:'Hold to alert your circle and share your live position. In a real emergency, contact local emergency services.',action:'I understand',input:false}));
$('#journeyBtn').addEventListener('click',()=>showModal({icon:'↗',title:'Themba’s journey',text:'8 minutes from home. Traffic is light and the route has no unusual stops.',action:'Done',input:false}));
$('#notificationBtn').addEventListener('click',()=>toast('You’re all caught up','No urgent family alerts.'));
$('#profileBtn').addEventListener('click',()=>toast('Your privacy is protected','Location is shared precisely with 3 people.'));
$('#mapModeBtn').addEventListener('click',()=>toast('Live map is on','Positions refresh as your circle moves.'));
$('#modalClose').addEventListener('click',()=>modal.close());
$('#modalAction').addEventListener('click',()=>{const input=$('#inviteInput');if(input.style.display!=='none'&&!input.value.includes('@')){input.focus();input.style.borderColor='#ed714b';return}modal.close();toast(input.style.display==='none'?'Safety comes first':'Invite sent',input.style.display==='none'?'Your SOS was not activated.':'They can join when they’re ready.');input.value=''});
modal.addEventListener('click',e=>{if(e.target===modal)modal.close()});
