# Objectifs :
Créer une calculatrice pouvant effectuer des opérations simples. L'application contiendra un côté client et un côté serveur qui communiqueront ensemble. Le client enverra une opération au serveur qui lui fera le calcul et renverra le résultat au client.

## Protocole de communication

### 1. Connexion
- **Protocole** : TCP
- **Port** : 8080
- **Adresse IP** : localhost
- **Encodage** : JSON (texte encodé en UTF-8)

### 2. Comportement
- Le client envoie une requête au serveur.
- Le serveur effectue le calcul demandé.
- Le serveur renvoie le résultat au client.
- Le client affiche le résultat.
- Le client peut fermer la connexion en envoyant le message `exit` ou demander une nouvelle opération.

### 3. Format des messages

#### Ligne de commande
- **Exemple de message** : `add 10 20`
- **Fermeture de la connexion** : `exit`

#### Requête client 
- **Exemple de message JSON** : 
  ```json
  {
    "operation": "add",
    "operand1" : 10,
    "operand2" : 20
  }
  ```
- **Champs** :
- `operation` : opération à effectuer (add, sub, mul, div)
- `operand1` : premier opérande
- `operand2` : deuxième opérande

#### Réponse serveur
- **Exemple de message JSON** : 
  ```json
  {
    "result": 30,
    "error": null
  }
  ```
- **Champs** :
- `result` : résultat de l'opération
- `error` : message d'erreur (null si pas d'erreur)

#### Gestion des erreurs
Si une erreur survient lors du calcul, l'utilisateur sera notifié par un message d'erreur et devra ressaisir une nouvelle opération.

- **Division par zéro** : 
  ```json
  {
    "result": null,
    "error": "Division by zero is not allowed"
  }
  ```
- **JSON invalide** : 
  ```json
  {
    "result": null,
    "error": "Invalid JSON"
  }
  ```
#### Gestion des erreurs côté client
Si une erreur est générée côté client, un message d'erreur sera affiché à l'utilisateur et il devra ressaisir une nouvelle opération.

- **Operation donné ne correspondant pas aux 4 opérations (add, sub, mul, div)** :`Invalid operation`
- **Operande 1 ou 2 n'est pas un nombre** : `Invalid operand`
- **Arguments manquants** : `Missing arguments`