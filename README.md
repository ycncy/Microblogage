<br/>
<p align="center">
  <a href="https://github.com/ycncy/Microblogage">
    <img src="https://cdn-icons-png.flaticon.com/512/5956/5956408.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Microblogage</h3>

</p>



# Description du projet

L'application est une application de microblogage (ou gazouillage). Les utilisateurs peuvent poster des courts messages, lire les derniers messages postés, s'abonner à d'autres utilisateurs (auquel cas ils reçoivent les messages de ces utilisateurs) et s'en désabonner. Les messages peuvent contenir des mots-clés (tags), et l'on peut consulter les messages récents contenant un mot-clé donné, ainsi que s'abonner/se désabonner à des/de mots-clés.

## Langage et outil utilisés

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white)

## Fonctionnement

L'interaction avec le serveur s'effectue par des échanges de type requête/réponse.

# Description détaillé des requêtes et des réponses

## Requêtes

##### Publier un message
entête : `PUBLISH author:@user`
corps : contenu du message
réponse : `OK` ou `ERROR`
`@user` est l'auteur du message. Le serveur renvoie une réponse `OK` ou `ERROR` décrite plus bas.


##### Recevoir des identifiants de messages

entête : `RCV_IDS [author:@user] [tag:#tag] [since_id:id] [limit:n]`
corps: vide
réponse: le serveur renvoie une réponse `MSG_IDS` contenant les identifiants des n messages les plus récents correspondant aux critères de la requête. 

Les identifiants sont ordonnés par ordre antichronologiques (les plus récents en premier).

(optionnel) `author:@user` l'auteur des messages est `@user`
(optionnel) `tag:#tag` les messages contiennent le mot clé `#tag`
(optionnel) `since_id:id` les messages ont été publiés après le message dont l'identifiant est `id`
(optionnel)` limit:n` aux plus n identifiants sont renvoyés. La valeur par défaut de `n` est `n=5`.

Ainsi, la requêtes `RCV_IDS` déclenchera une réponse `MSG_IDS` contenant les identifiants des 5 derniers messages publiés, les identifiants des messages les plus récents en premier.

##### Recevoir un message

entête : `RCV_MSG msg_id:id`
corps : vide
réponse : le serveur renvoie une réponse `MSG` contenant le message dont l'identifiant est `id` ou `ERROR` si il n'existe pas de message dont l'identifiant est `id`.

##### Publier un message en réponse à un autre
entête : `REPLY author:@user reply_to_id:id`
corps : contenu du message
réponse : `OK` ou `ERROR`

Le message est publié en réponse au message dont l'identifiant est id Le serveur renvoie une réponse OK ou ERROR.

##### Re-publier un message

entête : `REPUBLISH author:@user msg_id:id`
corps : vide
réponse : `OK` ou `ERROR`

Le message dont l'identifiant est id est publié de nouveau.

##### Se connecter
entête : `CONNECT user:@user`
corps : vide
réponse : `OK` ou `ERROR`.

Cette requête déclenche l'ouverture d'une connexion avec le serveur sur laquelle sera transmis le flux de messages auquel s'abonne le client. La gestion des abonnements se fait au moyen des requêtes `SUBSCRIBE` et `UNSUBSCRIBE`.

##### S'abonner
entête : `SUBSCRIBE author:@user et SUBSCRIBE tag:#tag`
corps : vide
réponse : `OK` ou `ERROR`. Le serveur envoie `ERROR` si ` @user` n'est pas géré par l'application. Si par contre le mot-clé #tag n'est pas encore géré, il est ajouté et `OK` est renvoyé.

Suite à cette requête, le serveur enverra une réponse `MSG ` sur la connexion précédemment ouverte par `CONNECT` à chaque fois qu'un message dont l'auteur est `@user` ou qui contient le mot-clé ` #tag` est publié.

##### Se désabonner
entête : `UNSUBSCRIBE author:@user et UNSUBSCRIBE tag:#tag`
corps : vide
réponse : `OK` ou `ERROR`. Le serveur renvoie `ERROR` si le client n'est pas abonné à `@user` ou au mot-clé `#tag`.

Suite à cette requête, le serveur cesse d'envoyer des messages dont l'auteur est `@user` ou qui contiennent le mot-clé ` #tag`.

## Réponses



##### Confirmation de publication

entête : `OK`
corps: vide renvoyée suite à une requête de publication d'un message

##### Signalement d'une erreur

entête : `ERROR`
corps : message d'erreur
Exemples de messages d'erreur : Bad request format, Unknown message id, etc .

##### Liste d'identifiants de messages

entête : `MSG_IDS`
corps : identifiants de messages, un par ligne, ordonnées par les plus récents en premier.

##### Message

entête : `MSG author:@user msg_id:id [reply_to_id:id][republished:true/false]`.

L'entête contient les méta-données du message, i.e. le nom de l'auteur, son identifiant, s'il s'agit d'un republication ou d'une réponse à un autre message. Les couples key:value sont séparés par un ou plusieurs espaces. Les couples entre crochet [ ] sont optionnels.

corps : le contenu du message.

## Contributeurs

* [Yacine Talhaoui](https://github.com/ycncy/)
* [Mattéo Lizero](https://github.com/AngryWalrusss)
