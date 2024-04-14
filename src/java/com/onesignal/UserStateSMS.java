package com.onesignal;

class UserStateSMS extends UserState
{
    private static final String SMS = "sms";
    
    UserStateSMS(final String s, final boolean b) {
        final StringBuilder sb = new StringBuilder("sms");
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
        return new UserStateSMS(s, false);
    }
}
