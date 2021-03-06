package pubg.radar.struct.cmd

import pubg.radar.deserializer.ROLE_MAX
import pubg.radar.deserializer.channel.ActorChannel
import pubg.radar.deserializer.channel.ActorChannel.Companion.actors
import pubg.radar.struct.*
import pubg.radar.struct.Archetype.*
import pubg.radar.struct.cmd.CMD.propertyVector100
import pubg.radar.struct.cmd.CMD.repMovement

object APawnCMD {
  fun process(actor: Actor, bunch: Bunch, repObj: NetGuidCacheObject?, waitingHandle: Int, data: HashMap<String, Any?>): Boolean {
    with(bunch) {
      when (waitingHandle) {
        1 -> if (readBit()) {//bHidden
          ActorChannel.visualActors.remove(actor.netGUID)
        }
        2 -> if (!readBit()) {// bReplicateMovement
          ActorChannel.visualActors.remove(actor.netGUID)
        }
        3 -> if (readBit()) {//bTearOff
          ActorChannel.visualActors.remove(actor.netGUID)
        }
        4 -> {
          val role = readInt(ROLE_MAX)
          val b = role
        }
        5 -> {
          val (netGUID, obj) = readObject()
          actor.owner = if (netGUID.isValid()) netGUID else null
        }
        6 -> {
          repMovement(actor)
          with(actor) {
            when (Type) {
              AirDrop -> ActorChannel.airDropLocation[netGUID] = location
              Other -> {
              }
              else -> ActorChannel.visualActors[netGUID] = this
            }
          }
        }
        7 -> {
          val (a, obj) = readObject()
          val attachTo = if (a.isValid()) {
            actors[a]?.attachChildren?.put(actor.netGUID, actor.netGUID)
            a
          } else null
          if (actor.attachParent != null)
            actors[actor.attachParent!!]?.attachChildren?.remove(actor.netGUID)
          actor.attachParent = attachTo
        }
        8 -> {
          val locationOffset = propertyVector100()
          if (actor.Type == DroopedItemGroup) {
          }
        }
        else -> return false
      }
      return true
    }
  }
}
