const ID_KEY = 'migme_guest_id';
const NAME_KEY = 'migme_guest_name';

function generateId() {
  return 'guest-' + Math.random().toString(36).slice(2, 9);
}

export function getGuestId() {
  let id = localStorage.getItem(ID_KEY);
  if (!id) {
    id = generateId();
    localStorage.setItem(ID_KEY, id);
  }
  return id;
}

export function getGuestName() {
  let name = localStorage.getItem(NAME_KEY);
  if (!name) {
    name = getGuestId();
    localStorage.setItem(NAME_KEY, name);
  }
  return name;
}

export function setGuestName(name) {
  localStorage.setItem(NAME_KEY, name);
}
