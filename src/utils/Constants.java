package utils;

import main.Game;


public class Constants {

    public static final float GRAVITY = 0.04f * Game.SCALE;
    public static final int ANI_SPEED = 25;

    public static class Projectiles{
        public static final int CANNONBALL_DEFAULT_WIDTH = 15;
        public static final int CANNONBALL_DEFAULT_HEIGHT = 15;

        public static final int CANNONBALL_WIDTH = (int)(CANNONBALL_DEFAULT_WIDTH * Game.SCALE);
        public static final int CANNONBALL_HEIGHT = (int)(CANNONBALL_DEFAULT_HEIGHT * Game.SCALE);

        public static final float PROJECTILE_SPEED = 0.8f * Game.SCALE;

    }

    public static class ObjectConstants{
        public static final int BOX = 1;
        public static final int BARREL = 2;
        public static final int SPIKE = 3;

        public static final int CONTAINER_WIDTH_DEFAULT = 40;
        public static final int CONTAINER_HEIGHT_DEFAULT = 30;
        public static final int CONTAINER_WIDTH = (int) (Game.SCALE * CONTAINER_WIDTH_DEFAULT);
        public static final int CONTAINER_HEIGHT = (int) (Game.SCALE * CONTAINER_HEIGHT_DEFAULT);

        public static final int SPIKE_WIDTH_DEFAULT = 32;
        public static final int SPIKE_HEIGHT_DEFAULT = 32;
        public static final int SPIKE_WIDTH = (int) (Game.SCALE * SPIKE_WIDTH_DEFAULT);
        public static final int SPIKE_HEIGHT = (int) (Game.SCALE * SPIKE_HEIGHT_DEFAULT);

        public static int GetSpriteAmount(int object_type){
            switch (object_type){
                case SPIKE:
                    return 1;
                case BARREL, BOX:
                    return 8;
            }
            return 1;
        }
    }

    public static class Directions{
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;

    }

    public static class PlayerConstants{

        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int JUMP = 2;
        public static final int FALLING = 3;
        public static final int ATTACK_1 = 4;
        public static final int HIT = 5;
        //public static final int ATTACK_JUMP_1 = 7;
        //public static final int ATTACK_JUMP_2 = 8;
        public static final int DEAD = 6;
        public static final int DASH = 7;

        //Knockback
        public static final float BASE_KNOCKBACK = 3.0f;
        public static final float KNOCKBACK_SCALING = 0.2f;

    }

    public static class UI{
        public static class Buttons{
            public static final int B_WIDTH_DEFAULT = 140;
            public static final int B_HEIGHT_DEFAULT = 56;
            public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
            public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);

            public static final int B_WIDTH_STONE_DEFAULT = 182;
            public static final int B_HEIGHT_STONE_DEFAULT = 64;
            public static final int B_STONE_WIDTH = (int) (B_WIDTH_STONE_DEFAULT * Game.SCALE);
            public static final int B_STONE_HEIGHT = (int) (B_HEIGHT_STONE_DEFAULT * Game.SCALE);

            public static final int B_WIDTH_STONE_SERVER_DEFAULT = 364;
            public static final int B_HEIGHT_STONE_SERVER_DEFAULT = 64;
            public static final int B_STONE_SERVER_WIDTH = (int) (B_WIDTH_STONE_SERVER_DEFAULT * Game.SCALE);
            public static final int B_STONE_SERVER_HEIGHT = (int) (B_HEIGHT_STONE_SERVER_DEFAULT * Game.SCALE);

            public static final int B_WIDTH_STONE_SELECT_DEFAULT = 96;
            public static final int B_HEIGHT_STONE_SELECT_DEFAULT = 64;
            public static final int B_STONE_SELECT_WIDTH = (int) (B_WIDTH_STONE_SELECT_DEFAULT * Game.SCALE);
            public static final int B_STONE_SELECT_HEIGHT = (int) (B_HEIGHT_STONE_SELECT_DEFAULT * Game.SCALE);
        }

        public static class PauseButtons{
            public static final int SOUND_SIZE_DEFAULT = 42;
            public static final int SOUND_SIZE = (int) (SOUND_SIZE_DEFAULT * Game.SCALE);
        }

        public static class URMButtons {
            public static final int URM_DEFAULT_SIZE = 64;
            public static  final int URM_SIZE = (int)(URM_DEFAULT_SIZE * Game.SCALE);
        }

        public static class VolumeButtons{
            public static final int VOLUME_DEFAULT_WIDTH = 28;
            public static final int VOLUME_DEFAULT_HEIGHT = 44;
            public static final int SLIDER_DEFAULT_WIDTH = 215;

            public static final int VOLUME_WIDTH = (int)(VOLUME_DEFAULT_WIDTH * Game.SCALE);
            public static final int VOLUME_HEIGHT = (int)(VOLUME_DEFAULT_HEIGHT * Game.SCALE);
            public static final int SLIDER_WIDTH = (int)(SLIDER_DEFAULT_WIDTH * Game.SCALE);
        }
    }

    public static class EnemyConstants{
        //enemy types
        public static final int ENEMY = 0;

        //enemy states
        public static final int IDLE = 0;
        public static final int RUNNING = 1;
        public static final int ATTACK = 2;
        public static final int HIT = 3;
        public static final int DEAD = 4;

        //ENEMY dimensions
        public static final int ENEMY_WIDTH_DEFAULT = 72;
        public static final int ENEMY_HEIGHT_DEFAULT = 32;
        public static final int ENEMY_WIDTH = (int) (ENEMY_WIDTH_DEFAULT * Game.SCALE);
        public static final int ENEMY_HEIGHT = (int) (ENEMY_HEIGHT_DEFAULT * Game.SCALE);

        //razlike početka sprita/hitboxa od početka img
        public static final int ENEMY_DRAWOFFSET_X = (int)(26 * Game.SCALE);
        public static final int ENEMY_DRAWOFFSET_Y = (int)(9 * Game.SCALE);

        public static int GetMaxHealth(int enemy_type){
            switch (enemy_type){
                case ENEMY:
                    return 10;
                default:
                    return 1;
            }
        }

        public static int GetEnemyDamage(int enemy_type){
            switch (enemy_type){
                case ENEMY:
                    return 15;
                default:
                    return 0;
            }
        }

        public static int GetSpriteAmount(int enemy_type, int enemy_state){
            switch (enemy_type){
                case ENEMY:
                    switch (enemy_state){
                        case IDLE:
                            return 9;
                        case RUNNING:
                            return 6;
                        case ATTACK:
                            return 7;
                        case HIT:
                            return 4;
                        case DEAD:
                            return 5;
                    }
            }
            return 0;
        }
    }

    public static class NetworkConstants{
        public static final int MAX_PLAYERS = 2;
    }
}
