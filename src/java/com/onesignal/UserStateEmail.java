package com.onesignal;

class UserStateEmail extends UserState
{
    private static final String EMAIL = "email";
    
    UserStateEmail(final String s, final boolean b) {
        final StringBuilder sb = new StringBuilder("email");
        sb.append(s);
        super(sb.toString(), b);
    }
    
    @Override
    protected void addDependFields() {
    }
    
    @Override
    boolean isSubscribed() {
        return true;
    }
    
    @Override
    UserState newInstance(final String s) {
        return new UserStateEmail(s, false);
    }
}
